package l.compiler

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File

/**
 * AST-driven JVM backend (0.0.4)
 * Supports:
 * - fun main() { ... }
 * - println("...")  (string literals)
 * - multiple println calls in order
 * - if/while: currently executes both sides / body once (branching next)
 */
class JvmBackend {
    fun emit(file: AstNode.File, className: String, outputDir: File) {
        outputDir.mkdirs()
        val messages = mutableListOf<String>()
        findMain(file)?.let { collectPrintlns(it.body, messages) }
        if (messages.isEmpty()) messages += "Hello from L!"
        val bytes = buildClass(className, messages)
        File(outputDir, "$className.class").writeBytes(bytes)
    }

    private fun findMain(file: AstNode.File): Decl.Function? {
        fun search(decls: List<Decl>): Decl.Function? {
            for (d in decls) {
                when (d) {
                    is Decl.Function -> if (d.name == "main") return d
                    is Decl.ClassLike -> search(d.members)?.let { return it }
                    else -> {}
                }
            }
            return null
        }
        return search(file.declarations)
    }

    private fun collectPrintlns(body: Body?, out: MutableList<String>) {
        when (body) {
            is Body.Block -> body.statements.forEach { collectStmt(it, out) }
            is Body.Expression -> collectExpr(body.expr, out)
            null -> {}
        }
    }

    private fun collectStmt(s: Stmt, out: MutableList<String>) {
        when (s) {
            is Stmt.ExprStmt -> collectExpr(s.expr, out)
            is Stmt.Return -> s.expr?.let { collectExpr(it, out) }
            is Stmt.LocalVar -> s.init?.let { collectExpr(it, out) }
            is Stmt.If -> {
                collectExpr(s.condition, out)
                s.thenBranch.forEach { collectStmt(it, out) }
                s.elseBranch?.forEach { collectStmt(it, out) }
            }
            is Stmt.While -> {
                collectExpr(s.condition, out)
                s.body.forEach { collectStmt(it, out) }
            }
            Stmt.Empty -> {}
        }
    }

    private fun collectExpr(e: Expr, out: MutableList<String>) {
        when (e) {
            is Expr.Call -> {
                val name = when (val c = e.callee) {
                    is Expr.Ident -> c.name
                    is Expr.Member -> c.name
                    else -> null
                }
                if (name == "println" && e.args.size == 1) {
                    when (val a = e.args[0]) {
                        is Expr.Literal -> out += a.value
                        else -> {}
                    }
                }
                e.args.forEach { collectExpr(it, out) }
            }
            is Expr.Binary -> {
                collectExpr(e.left, out)
                collectExpr(e.right, out)
            }
            is Expr.Unary -> collectExpr(e.expr, out)
            is Expr.Member -> collectExpr(e.receiver, out)
            is Expr.Index -> {
                collectExpr(e.receiver, out)
                collectExpr(e.index, out)
            }
            is Expr.Assign -> {
                collectExpr(e.target, out)
                collectExpr(e.value, out)
            }
            else -> {}
        }
    }

    private fun buildClass(className: String, messages: List<String>): ByteArray {
        val p = ConstantPool()
        val thisUtf = p.utf8(className)
        val thisCl = p.classInfo(thisUtf)
        val objUtf = p.utf8("java/lang/Object")
        val superCl = p.classInfo(objUtf)
        val mainU = p.utf8("main")
        val mainD = p.utf8("([Ljava/lang/String;)V")
        val codeU = p.utf8("Code")
        val sysU = p.utf8("java/lang/System")
        val sysC = p.classInfo(sysU)
        val outU = p.utf8("out")
        val outD = p.utf8("Ljava/io/PrintStream;")
        val outNt = p.nameAndType(outU, outD)
        val outF = p.fieldRef(sysC, outNt)
        val psU = p.utf8("java/io/PrintStream")
        val psC = p.classInfo(psU)
        val prU = p.utf8("println")
        val prD = p.utf8("(Ljava/lang/String;)V")
        val prNt = p.nameAndType(prU, prD)
        val prRef = p.methodRef(psC, prNt)

        val msgIdx = messages.map { msg ->
            val u = p.utf8(msg)
            p.string(u)
        }

        val code = ByteArrayOutputStream()
        for (mi in msgIdx) {
            code.write(0xB2)
            code.write(outF shr 8)
            code.write(outF and 0xFF)
            if (mi <= 255) {
                code.write(0x12)
                code.write(mi)
            } else {
                code.write(0x13)
                code.write(mi shr 8)
                code.write(mi and 0xFF)
            }
            code.write(0xB6)
            code.write(prRef shr 8)
            code.write(prRef and 0xFF)
        }
        code.write(0xB1)
        val codeBytes = code.toByteArray()

        val baos = ByteArrayOutputStream()
        val out = DataOutputStream(baos)
        out.writeInt(0xCAFEBABE.toInt())
        out.writeShort(0)
        out.writeShort(52)
        p.write(out)
        out.writeShort(0x0001)
        out.writeShort(thisCl)
        out.writeShort(superCl)
        out.writeShort(0)
        out.writeShort(0)
        out.writeShort(1)
        out.writeShort(0x0009)
        out.writeShort(mainU)
        out.writeShort(mainD)
        out.writeShort(1)
        out.writeShort(codeU)
        out.writeInt(2 + 2 + 4 + codeBytes.size + 2 + 2)
        out.writeShort(2)
        out.writeShort(1)
        out.writeInt(codeBytes.size)
        out.write(codeBytes)
        out.writeShort(0)
        out.writeShort(0)
        out.writeShort(0)
        return baos.toByteArray()
    }
}

private class ConstantPool {
    private val items = mutableListOf<ByteArray>()
    fun utf8(s: String): Int {
        val b = s.toByteArray(Charsets.UTF_8)
        val e = ByteArray(3 + b.size)
        e[0] = 1
        e[1] = (b.size shr 8).toByte()
        e[2] = (b.size and 0xFF).toByte()
        System.arraycopy(b, 0, e, 3, b.size)
        items += e
        return items.size
    }
    fun classInfo(n: Int): Int {
        items += byteArrayOf(7, (n shr 8).toByte(), (n and 0xFF).toByte())
        return items.size
    }
    fun string(u: Int): Int {
        items += byteArrayOf(8, (u shr 8).toByte(), (u and 0xFF).toByte())
        return items.size
    }
    fun nameAndType(n: Int, d: Int): Int {
        items += byteArrayOf(12, (n shr 8).toByte(), (n and 0xFF).toByte(), (d shr 8).toByte(), (d and 0xFF).toByte())
        return items.size
    }
    fun fieldRef(c: Int, nt: Int): Int {
        items += byteArrayOf(9, (c shr 8).toByte(), (c and 0xFF).toByte(), (nt shr 8).toByte(), (nt and 0xFF).toByte())
        return items.size
    }
    fun methodRef(c: Int, nt: Int): Int {
        items += byteArrayOf(10, (c shr 8).toByte(), (c and 0xFF).toByte(), (nt shr 8).toByte(), (nt and 0xFF).toByte())
        return items.size
    }
    fun write(out: DataOutputStream) {
        out.writeShort(items.size + 1)
        items.forEach { out.write(it) }
    }
}