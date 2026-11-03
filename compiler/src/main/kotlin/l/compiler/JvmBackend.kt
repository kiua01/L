package l.compiler

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File

/**
 * JVM backend 0.0.10
 * - multiple top-level functions as public static methods
 * - calls between them (Int/Boolean/String/Unit, simple params)
 * - string concat, println, if/while/when, locals
 */
class JvmBackend {
    fun emit(file: AstNode.File, className: String, outputDir: File) {
        outputDir.mkdirs()
        val functions = collectFunctions(file)
        if (functions.isEmpty()) {
            val cg = CodeGen(emptyMap())
            cg.emitReturn()
            File(outputDir, "$className.class").writeBytes(ClassFile(className).build(listOf(MethodEmit("main", "([Ljava/lang/String;)V", cg))))
            return
        }
        val sigs = functions.associate { it.name to descriptorOf(it) }
        val methods = functions.map { fn ->
            val cg = CodeGen(sigs)
            // params as locals 0..n-1 for static methods (main keeps slot 0 for String[] if main)
            if (fn.name == "main") {
                cg.reserveMainArgs()
            } else {
                fn.params.forEachIndexed { i, p ->
                    cg.defineParam(p.name, p.type ?: "Int", i)
                }
                cg.setNextLocal(fn.params.size)
            }
            cg.emitBody(fn.body)
            MethodEmit(fn.name, sigs[fn.name] ?: "()V", cg)
        }
        // ensure main exists
        val hasMain = methods.any { it.name == "main" }
        val all = if (hasMain) methods else {
            val cg = CodeGen(sigs)
            cg.emitReturn()
            methods + MethodEmit("main", "([Ljava/lang/String;)V", cg)
        }
        File(outputDir, "$className.class").writeBytes(ClassFile(className).build(all))
    }

    private fun collectFunctions(file: AstNode.File): List<Decl.Function> {
        val out = mutableListOf<Decl.Function>()
        fun walk(decls: List<Decl>) {
            for (d in decls) {
                when (d) {
                    is Decl.Function -> out += d
                    is Decl.ClassLike -> walk(d.members)
                    else -> {}
                }
            }
        }
        walk(file.declarations)
        return out
    }

    private fun descriptorOf(fn: Decl.Function): String {
        if (fn.name == "main") return "([Ljava/lang/String;)V"
        val params = fn.params.joinToString("") { jvmType(it.type ?: "Int") }
        val ret = jvmType(fn.returnType ?: "Unit")
        return "($params)$ret"
    }

    companion object {
        fun jvmType(t: String): String = when (t) {
            "Int", "Boolean" -> "I"
            "String" -> "Ljava/lang/String;"
            "Unit", "void" -> "V"
            else -> "I"
        }
    }
}

data class MethodEmit(val name: String, val descriptor: String, val cg: CodeGen)

internal class CodeGen(private val functionSigs: Map<String, String>) {
    private val code = mutableListOf<Int>()
    private val locals = linkedMapOf<String, Int>()
    private val localTypes = linkedMapOf<String, String>()
    private var nextLocal = 0
    private val labels = mutableListOf<Label>()
    val prints = mutableListOf<PrintSite>()
    val stringConsts = mutableListOf<String>()
    val calls = mutableListOf<CallSite>() // method call markers

    data class CallSite(val name: String, val descriptor: String)

    sealed class PrintSite {
        data class Str(val s: String) : PrintSite()
        data class IntConst(val v: Int) : PrintSite()
        data class IntLocal(val slot: Int) : PrintSite()
        data class StringLocal(val slot: Int) : PrintSite()
    }

    class Label {
        var at = -1
        val patches = mutableListOf<Int>()
    }

    fun reserveMainArgs() {
        nextLocal = 1
    }

    fun defineParam(name: String, type: String, slot: Int) {
        locals[name] = slot
        localTypes[name] = when (type) {
            "Boolean" -> "Boolean"
            "String" -> "String"
            else -> "Int"
        }
    }

    fun setNextLocal(n: Int) {
        nextLocal = n
    }

    fun emitBody(body: Body?) {
        when (body) {
            is Body.Block -> body.statements.forEach { emitStmt(it) }
            is Body.Expression -> {
                emitExpr(body.expr)
                // if expression body of non-void, return handled by caller signature later
            }
            null -> {}
        }
        emitReturn()
        patch()
    }

    fun emitReturn() {
        emit(0xB1)
    }

    private fun patch() {
        for (l in labels) {
            for (p in l.patches) {
                val off = l.at - p
                code[p + 1] = (off shr 8) and 0xFF
                code[p + 2] = off and 0xFF
            }
        }
    }

    private fun allocLocal(name: String, type: String): Int {
        localTypes[name] = type
        return locals.getOrPut(name) { nextLocal++ }
    }

    private fun inferExprType(e: Expr): String = when (e) {
        is Expr.Literal -> when (e.kind) {
            LitKind.INT -> "Int"
            LitKind.BOOL -> "Boolean"
            LitKind.STRING -> "String"
            else -> "Int"
        }
        is Expr.Ident -> localTypes[e.name] ?: "Int"
        is Expr.Binary -> when (e.op) {
            "==", "!=", "<", ">", "<=", ">=", "&&", "||" -> "Boolean"
            "+" -> {
                val l = inferExprType(e.left)
                val r = inferExprType(e.right)
                if (l == "String" || r == "String") "String" else "Int"
            }
            else -> "Int"
        }
        is Expr.Unary -> if (e.op == "!") "Boolean" else "Int"
        is Expr.Call -> {
            val name = (e.callee as? Expr.Ident)?.name
            val sig = name?.let { functionSigs[it] }
            when {
                sig == null -> "Unit"
                sig.endsWith(")I") -> "Int"
                sig.endsWith(")Ljava/lang/String;") -> "String"
                else -> "Unit"
            }
        }
        else -> "Int"
    }

    private fun emitStmt(s: Stmt) {
        when (s) {
            is Stmt.LocalVar -> {
                val ty = s.type ?: s.init?.let { inferExprType(it) } ?: "Int"
                val slot = allocLocal(s.name, ty)
                if (s.init != null) emitExpr(s.init) else if (ty == "String") emit(0x01) else emit(0x03)
                if (ty == "String") emit(0x3A, slot) else emit(0x36, slot)
            }
            is Stmt.ExprStmt -> emitExpr(s.expr)
            is Stmt.Return -> {
                if (s.expr != null) {
                    emitExpr(s.expr)
                    val ty = inferExprType(s.expr)
                    when (ty) {
                        "String" -> emit(0xB0) // areturn
                        "Unit" -> emit(0xB1)
                        else -> emit(0xAC) // ireturn
                    }
                } else emit(0xB1)
            }
            is Stmt.If -> {
                emitExpr(s.condition)
                val elseL = label(); val endL = label()
                branch(0x99, elseL)
                s.thenBranch.forEach { emitStmt(it) }
                branch(0xA7, endL)
                mark(elseL)
                s.elseBranch?.forEach { emitStmt(it) }
                mark(endL)
            }
            is Stmt.While -> {
                val start = label(); val end = label()
                mark(start)
                emitExpr(s.condition)
                branch(0x99, end)
                s.body.forEach { emitStmt(it) }
                branch(0xA7, start)
                mark(end)
            }
            is Stmt.When -> {
                val end = label()
                val subjectSlot = if (s.subject != null) {
                    val slot = nextLocal++
                    emitExpr(s.subject)
                    emit(0x36, slot)
                    slot
                } else null
                for (entry in s.entries) {
                    val next = label()
                    if (entry.conditions == null) {
                        entry.body.forEach { emitStmt(it) }
                        branch(0xA7, end)
                    } else {
                        val match = label()
                        for (c in entry.conditions) {
                            if (subjectSlot != null) {
                                emit(0x15, subjectSlot)
                                emitExpr(c)
                                emit(0x64)
                                branch(0x99, match)
                            } else {
                                emitExpr(c)
                                branch(0x9A, match)
                            }
                        }
                        branch(0xA7, next)
                        mark(match)
                        entry.body.forEach { emitStmt(it) }
                        branch(0xA7, end)
                        mark(next)
                    }
                }
                mark(end)
            }
            Stmt.Empty -> {}
        }
    }

    private fun emitExpr(e: Expr) {
        when (e) {
            is Expr.Literal -> when (e.kind) {
                LitKind.INT -> pushInt(e.value.toInt())
                LitKind.BOOL -> emit(if (e.value == "true") 0x04 else 0x03)
                LitKind.STRING -> {
                    stringConsts += e.value
                    emit(0xFD, stringConsts.size - 1)
                }
                else -> emit(0x03)
            }
            is Expr.Ident -> {
                val slot = locals[e.name]
                val ty = localTypes[e.name] ?: "Int"
                if (slot != null) {
                    if (ty == "String") emit(0x19, slot) else emit(0x15, slot)
                } else emit(0x03)
            }
            is Expr.Unary -> {
                emitExpr(e.expr)
                when (e.op) {
                    "-" -> emit(0x74)
                    "!" -> {
                        val z = label(); val end = label()
                        branch(0x99, z)
                        emit(0x03)
                        branch(0xA7, end)
                        mark(z)
                        emit(0x04)
                        mark(end)
                    }
                }
            }
            is Expr.Binary -> when (e.op) {
                "+" -> {
                    val lt = inferExprType(e.left)
                    val rt = inferExprType(e.right)
                    if (lt == "String" || rt == "String") emitStringConcat(e.left, e.right)
                    else {
                        emitExpr(e.left); emitExpr(e.right); emit(0x60)
                    }
                }
                "-", "*", "/" -> {
                    emitExpr(e.left); emitExpr(e.right)
                    emit(when (e.op) { "-" -> 0x64; "*" -> 0x68; else -> 0x6C })
                }
                "==", "!=", "<", ">", "<=", ">=" -> {
                    emitExpr(e.left); emitExpr(e.right); emit(0x64)
                    val t = label(); val end = label()
                    branch(when (e.op) {
                        "==" -> 0x99; "!=" -> 0x9A; "<" -> 0x9B
                        ">" -> 0x9C; "<=" -> 0x9E; else -> 0x9D
                    }, t)
                    emit(0x03); branch(0xA7, end); mark(t); emit(0x04); mark(end)
                }
                "&&", "||" -> {
                    emitExpr(e.left); emitExpr(e.right)
                    emit(if (e.op == "&&") 0x7E else 0x80)
                }
                else -> emit(0x03)
            }
            is Expr.Call -> {
                val name = when (val c = e.callee) {
                    is Expr.Ident -> c.name
                    is Expr.Member -> c.name
                    else -> null
                }
                if (name == "println") {
                    emitPrintln(e.args.getOrNull(0))
                } else if (name != null && functionSigs.containsKey(name)) {
                    val desc = functionSigs[name]!!
                    e.args.forEach { emitExpr(it) }
                    calls += CallSite(name, desc)
                    emit(0xFE, calls.size - 1) // invokestatic marker
                } else {
                    e.args.forEach { emitExpr(it) }
                }
            }
            is Expr.Assign -> {
                if (e.target is Expr.Ident) {
                    val ty = localTypes[e.target.name] ?: inferExprType(e.value)
                    val slot = allocLocal(e.target.name, ty)
                    emitExpr(e.value)
                    if (ty == "String") {
                        emit(0x3A, slot); emit(0x19, slot)
                    } else {
                        emit(0x36, slot); emit(0x15, slot)
                    }
                } else emitExpr(e.value)
            }
            else -> emit(0x03)
        }
    }

    private fun emitStringConcat(left: Expr, right: Expr) {
        emit(0xFA)
        appendPart(left)
        appendPart(right)
        emit(0xF6)
    }

    private fun appendPart(e: Expr) {
        when (e) {
            is Expr.Literal -> when (e.kind) {
                LitKind.STRING, LitKind.BOOL -> {
                    stringConsts += e.value
                    emit(0xF9, stringConsts.size - 1)
                }
                LitKind.INT -> emit(0xF5, e.value.toInt() and 0xFF)
                else -> {
                    stringConsts += ""
                    emit(0xF9, stringConsts.size - 1)
                }
            }
            is Expr.Ident -> {
                val slot = locals[e.name]
                val ty = localTypes[e.name]
                if (slot != null) {
                    if (ty == "String") emit(0xF8, slot) else emit(0xF4, slot)
                } else {
                    stringConsts += ""
                    emit(0xF9, stringConsts.size - 1)
                }
            }
            else -> {
                stringConsts += "?"
                emit(0xF9, stringConsts.size - 1)
            }
        }
    }

    private fun emitPrintln(arg: Expr?) {
        when (arg) {
            is Expr.Literal -> when (arg.kind) {
                LitKind.INT -> {
                    prints += PrintSite.IntConst(arg.value.toInt())
                    emit(0xFC, prints.size - 1)
                }
                else -> {
                    prints += PrintSite.Str(arg.value)
                    emit(0xFC, prints.size - 1)
                }
            }
            is Expr.Ident -> {
                val slot = locals[arg.name]
                val ty = localTypes[arg.name]
                if (slot != null) {
                    if (ty == "String") {
                        prints += PrintSite.StringLocal(slot)
                        emit(0xFC, prints.size - 1)
                    } else {
                        prints += PrintSite.IntLocal(slot)
                        emit(0xFC, prints.size - 1)
                    }
                }
            }
            else -> arg?.let { emitExpr(it); emit(0x57) }
        }
    }

    private fun pushInt(v: Int) {
        when (v) {
            -1 -> emit(0x02)
            in 0..5 -> emit(0x03 + v)
            in -128..127 -> emit(0x10, v)
            in -32768..32767 -> emit(0x11, (v shr 8) and 0xFF, v and 0xFF)
            else -> emit(0x03)
        }
    }

    private fun label() = Label().also { labels += it }
    private fun mark(l: Label) { l.at = code.size }
    private fun branch(opc: Int, l: Label) {
        val at = code.size
        code += opc; code += 0; code += 0
        l.patches += at
    }
    private fun emit(vararg bs: Int) { bs.forEach { code += it and 0xFF } }

    fun rawBytes() = code.map { it.toByte() }.toByteArray()
    fun maxLocals() = nextLocal.coerceAtLeast(1)
}

internal class ClassFile(private val className: String) {
    fun build(methods: List<MethodEmit>): ByteArray {
        val p = Pool()
        val thisC = p.cls(p.utf8(className))
        val superC = p.cls(p.utf8("java/lang/Object"))
        val codeU = p.utf8("Code")
        val sysC = p.cls(p.utf8("java/lang/System"))
        val outF = p.field(sysC, p.nat(p.utf8("out"), p.utf8("Ljava/io/PrintStream;")))
        val psC = p.cls(p.utf8("java/io/PrintStream"))
        val prStr = p.method(psC, p.nat(p.utf8("println"), p.utf8("(Ljava/lang/String;)V")))
        val prInt = p.method(psC, p.nat(p.utf8("println"), p.utf8("(I)V")))
        val sbC = p.cls(p.utf8("java/lang/StringBuilder"))
        val sbInit = p.method(sbC, p.nat(p.utf8("<init>"), p.utf8("()V")))
        val sbAppendStr = p.method(sbC, p.nat(p.utf8("append"), p.utf8("(Ljava/lang/String;)Ljava/lang/StringBuilder;")))
        val sbAppendInt = p.method(sbC, p.nat(p.utf8("append"), p.utf8("(I)Ljava/lang/StringBuilder;")))
        val sbToString = p.method(sbC, p.nat(p.utf8("toString"), p.utf8("()Ljava/lang/String;")))

        // Pre-register method refs for calls
        val methodRefs = mutableMapOf<Pair<String, String>, Int>()
        fun methRef(name: String, desc: String): Int =
            methodRefs.getOrPut(name to desc) {
                p.method(thisC, p.nat(p.utf8(name), p.utf8(desc)))
            }
        methods.forEach { methRef(it.name, it.descriptor) }
        methods.forEach { m -> m.cg.calls.forEach { methRef(it.name, it.descriptor) } }

        val methodNameIdx = methods.associate { it.name to p.utf8(it.name) }
        val methodDescIdx = methods.associate { it.descriptor to p.utf8(it.descriptor) }

        fun expandMethod(cg: CodeGen): ByteArray {
            val strIdx = mutableMapOf<String, Int>()
            fun s(v: String) = strIdx.getOrPut(v) { p.string(p.utf8(v)) }
            cg.prints.filterIsInstance<CodeGen.PrintSite.Str>().forEach { s(it.s) }
            cg.stringConsts.forEach { s(it) }

            val raw = cg.rawBytes().map { it.toInt() and 0xFF }

            fun expandLen(oi: Int): Int = when (raw[oi]) {
                0xFC -> when (val site = cg.prints[raw[oi + 1]]) {
                    is CodeGen.PrintSite.Str, is CodeGen.PrintSite.IntLocal, is CodeGen.PrintSite.StringLocal -> 8
                    is CodeGen.PrintSite.IntConst -> {
                        val v = site.v
                        3 + (when (v) { in 0..5 -> 1; in -128..127 -> 2; else -> 3 }) + 3
                    }
                }
                0xFD -> 2
                0xFA -> 8
                0xF9, 0xF8, 0xF5, 0xF4 -> 6
                0xF6 -> 3
                0xFE -> 3 // invokestatic
                else -> 1
            }
            fun markerWidth(op: Int) = when (op) {
                0xFC, 0xFD, 0xF9, 0xF8, 0xF5, 0xF4, 0xFE -> 2
                0xFA, 0xF6 -> 1
                else -> 1
            }

            val map = IntArray(raw.size + 1)
            var ni = 0; var oi = 0
            while (oi < raw.size) {
                map[oi] = ni
                val op = raw[oi]
                if (op in listOf(0xFC, 0xFD, 0xFA, 0xF9, 0xF8, 0xF5, 0xF4, 0xF6, 0xFE)) {
                    ni += expandLen(oi); oi += markerWidth(op)
                } else { ni += 1; oi += 1 }
            }
            map[raw.size] = ni

            val expanded = mutableListOf<Int>()
            oi = 0
            while (oi < raw.size) {
                when (val op = raw[oi]) {
                    0xFC -> {
                        val site = cg.prints[raw[oi + 1]]
                        expanded += 0xB2; expanded += (outF shr 8) and 0xFF; expanded += outF and 0xFF
                        when (site) {
                            is CodeGen.PrintSite.Str -> {
                                val si = s(site.s)
                                expanded += 0x12; expanded += si and 0xFF
                                expanded += 0xB6; expanded += (prStr shr 8) and 0xFF; expanded += prStr and 0xFF
                            }
                            is CodeGen.PrintSite.IntConst -> {
                                val v = site.v
                                when (v) {
                                    in 0..5 -> expanded += 0x03 + v
                                    in -128..127 -> { expanded += 0x10; expanded += v and 0xFF }
                                    else -> { expanded += 0x11; expanded += (v shr 8) and 0xFF; expanded += v and 0xFF }
                                }
                                expanded += 0xB6; expanded += (prInt shr 8) and 0xFF; expanded += prInt and 0xFF
                            }
                            is CodeGen.PrintSite.IntLocal -> {
                                expanded += 0x15; expanded += site.slot
                                expanded += 0xB6; expanded += (prInt shr 8) and 0xFF; expanded += prInt and 0xFF
                            }
                            is CodeGen.PrintSite.StringLocal -> {
                                expanded += 0x19; expanded += site.slot
                                expanded += 0xB6; expanded += (prStr shr 8) and 0xFF; expanded += prStr and 0xFF
                            }
                        }
                        oi += 2
                    }
                    0xFD -> { val si = s(cg.stringConsts[raw[oi + 1]]); expanded += 0x12; expanded += si and 0xFF; oi += 2 }
                    0xFA -> {
                        expanded += 0xBB; expanded += (sbC shr 8) and 0xFF; expanded += sbC and 0xFF
                        expanded += 0x59
                        expanded += 0xB7; expanded += (sbInit shr 8) and 0xFF; expanded += sbInit and 0xFF
                        oi += 1
                    }
                    0xF9 -> {
                        val si = s(cg.stringConsts[raw[oi + 1]])
                        expanded += 0x12; expanded += si and 0xFF
                        expanded += 0xB6; expanded += (sbAppendStr shr 8) and 0xFF; expanded += sbAppendStr and 0xFF
                        oi += 2
                    }
                    0xF8 -> {
                        expanded += 0x19; expanded += raw[oi + 1]
                        expanded += 0xB6; expanded += (sbAppendStr shr 8) and 0xFF; expanded += sbAppendStr and 0xFF
                        oi += 2
                    }
                    0xF5 -> {
                        expanded += 0x10; expanded += raw[oi + 1]
                        expanded += 0xB6; expanded += (sbAppendInt shr 8) and 0xFF; expanded += sbAppendInt and 0xFF
                        oi += 2
                    }
                    0xF4 -> {
                        expanded += 0x15; expanded += raw[oi + 1]
                        expanded += 0xB6; expanded += (sbAppendInt shr 8) and 0xFF; expanded += sbAppendInt and 0xFF
                        oi += 2
                    }
                    0xF6 -> {
                        expanded += 0xB6; expanded += (sbToString shr 8) and 0xFF; expanded += sbToString and 0xFF
                        oi += 1
                    }
                    0xFE -> {
                        val call = cg.calls[raw[oi + 1]]
                        val ref = methRef(call.name, call.descriptor)
                        expanded += 0xB8 // invokestatic
                        expanded += (ref shr 8) and 0xFF; expanded += ref and 0xFF
                        oi += 2
                    }
                    else -> { expanded += op; oi += 1 }
                }
            }

            fun isBranch(op: Int) =
                op == 0x99 || op == 0x9A || op == 0x9B || op == 0x9C || op == 0x9D || op == 0x9E || op == 0xA7
            oi = 0; var ei = 0
            while (oi < raw.size) {
                val op = raw[oi]
                if (op in listOf(0xFC, 0xFD, 0xFA, 0xF9, 0xF8, 0xF5, 0xF4, 0xF6, 0xFE)) {
                    ei += expandLen(oi); oi += markerWidth(op)
                } else if (isBranch(op) && oi + 2 < raw.size) {
                    val oldOff = ((raw[oi + 1] shl 8) or raw[oi + 2]).toShort().toInt()
                    val targetOld = (oi + oldOff).coerceIn(0, raw.size)
                    val newOff = map[targetOld] - ei
                    expanded[ei + 1] = (newOff shr 8) and 0xFF
                    expanded[ei + 2] = newOff and 0xFF
                    ei += 1; oi += 1
                } else { ei += 1; oi += 1 }
            }
            return expanded.map { it.toByte() }.toByteArray()
        }

        val baos = ByteArrayOutputStream()
        val out = DataOutputStream(baos)
        out.writeInt(0xCAFEBABE.toInt())
        out.writeShort(0)
        out.writeShort(49)

        // We need pool complete before writing — build method codes first which may add pool entries
        val methodCodes = methods.map { it to expandMethod(it.cg) }

        p.write(out)
        out.writeShort(0x0001)
        out.writeShort(thisC)
        out.writeShort(superC)
        out.writeShort(0)
        out.writeShort(0)
        out.writeShort(methods.size)
        for ((m, codeBytes) in methodCodes) {
            out.writeShort(0x0009) // public static
            out.writeShort(methodNameIdx[m.name]!!)
            out.writeShort(methodDescIdx[m.descriptor]!!)
            out.writeShort(1)
            out.writeShort(codeU)
            out.writeInt(2 + 2 + 4 + codeBytes.size + 2 + 2)
            out.writeShort(16)
            out.writeShort(m.cg.maxLocals())
            out.writeInt(codeBytes.size)
            out.write(codeBytes)
            out.writeShort(0)
            out.writeShort(0)
        }
        out.writeShort(0)
        return baos.toByteArray()
    }
}

private class Pool {
    private val items = mutableListOf<ByteArray>()
    fun utf8(s: String): Int {
        val b = s.toByteArray(Charsets.UTF_8)
        val e = ByteArray(3 + b.size)
        e[0] = 1; e[1] = (b.size shr 8).toByte(); e[2] = (b.size and 0xFF).toByte()
        System.arraycopy(b, 0, e, 3, b.size)
        items += e
        return items.size
    }
    fun cls(n: Int): Int { items += byteArrayOf(7, (n shr 8).toByte(), (n and 0xFF).toByte()); return items.size }
    fun string(u: Int): Int { items += byteArrayOf(8, (u shr 8).toByte(), (u and 0xFF).toByte()); return items.size }
    fun nat(n: Int, d: Int): Int {
        items += byteArrayOf(12, (n shr 8).toByte(), (n and 0xFF).toByte(), (d shr 8).toByte(), (d and 0xFF).toByte())
        return items.size
    }
    fun field(c: Int, nt: Int): Int {
        items += byteArrayOf(9, (c shr 8).toByte(), (c and 0xFF).toByte(), (nt shr 8).toByte(), (nt and 0xFF).toByte())
        return items.size
    }
    fun method(c: Int, nt: Int): Int {
        items += byteArrayOf(10, (c shr 8).toByte(), (c and 0xFF).toByte(), (nt shr 8).toByte(), (nt and 0xFF).toByte())
        return items.size
    }
    fun write(out: DataOutputStream) {
        out.writeShort(items.size + 1)
        items.forEach { out.write(it) }
    }
}
