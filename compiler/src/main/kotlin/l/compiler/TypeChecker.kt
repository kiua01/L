package l.compiler

/**
 * Minimal type checker for a subset of L.
 * Infers types for literals and simple expressions; checks basic mismatches.
 */
class TypeChecker(private val diagnostics: DiagnosticBag) {

    private val scopes = ArrayDeque<MutableMap<String, String>>()

    fun check(file: AstNode.File) {
        push()
        // predefined
        current()["true"] = "Boolean"
        current()["false"] = "Boolean"
        for (d in file.declarations) checkDecl(d)
        pop()
    }

    private fun checkDecl(d: Decl) {
        when (d) {
            is Decl.Function -> {
                push()
                for (p in d.params) {
                    val t = p.type ?: "Any"
                    current()[p.name] = t
                }
                when (val body = d.body) {
                    is Body.Block -> body.statements.forEach { checkStmt(it, d.returnType) }
                    is Body.Expression -> {
                        val t = infer(body.expr)
                        if (d.returnType != null && !compatible(t, d.returnType)) {
                            diagnostics.error("Return type mismatch: expected ${d.returnType}, got $t")
                        }
                    }
                    null -> {}
                }
                pop()
                current()[d.name] = "Function"
            }
            is Decl.Property -> {
                val inferred = d.initializer?.let { infer(it) }
                val t = d.type ?: inferred ?: "Any"
                if (d.type != null && inferred != null && !compatible(inferred, d.type)) {
                    diagnostics.error("Property '${d.name}' type mismatch: expected ${d.type}, got $inferred")
                }
                current()[d.name] = t
            }
            is Decl.ClassLike -> {
                current()[d.name] = d.kind
                push()
                d.members.forEach { checkDecl(it) }
                pop()
            }
            is Decl.TypeAlias -> current()[d.name] = d.type ?: "Any"
        }
    }

    private fun checkStmt(s: Stmt, expectedReturn: String?) {
        when (s) {
            is Stmt.LocalVar -> {
                val inferred = s.init?.let { infer(it) }
                val t = s.type ?: inferred ?: "Any"
                if (s.type != null && inferred != null && !compatible(inferred, s.type)) {
                    diagnostics.error("Local '${s.name}' type mismatch: expected ${s.type}, got $inferred")
                }
                current()[s.name] = t
            }
            is Stmt.Return -> {
                val t = s.expr?.let { infer(it) } ?: "Unit"
                if (expectedReturn != null && !compatible(t, expectedReturn)) {
                    diagnostics.error("Return type mismatch: expected $expectedReturn, got $t")
                }
            }
            is Stmt.ExprStmt -> infer(s.expr)
            Stmt.Empty -> {}
        }
    }

    private fun infer(e: Expr): String = when (e) {
        is Expr.Literal -> when (e.kind) {
            LitKind.INT -> "Int"
            LitKind.FLOAT -> "Double"
            LitKind.STRING -> "String"
            LitKind.CHAR -> "Char"
            LitKind.BOOL -> "Boolean"
            LitKind.NULL -> "Nothing?"
        }
        is Expr.Ident -> lookup(e.name) ?: run {
            diagnostics.warning("Unresolved reference '${e.name}'")
            "Any"
        }
        is Expr.Unary -> {
            val t = infer(e.expr)
            when (e.op) {
                "!" -> "Boolean"
                "-", "+" -> t
                else -> "Any"
            }
        }
        is Expr.Binary -> {
            val lt = infer(e.left)
            val rt = infer(e.right)
            when (e.op) {
                "+", "-", "*", "/", "%" -> when {
                    lt == "String" || rt == "String" -> "String"
                    lt == "Double" || rt == "Double" -> "Double"
                    else -> "Int"
                }
                "==", "!=", "<", ">", "<=", ">=", "&&", "||" -> "Boolean"
                ".." -> "IntRange"
                else -> "Any"
            }
        }
        is Expr.Call -> {
            infer(e.callee)
            e.args.forEach { infer(it) }
            "Any"
        }
        is Expr.Member -> {
            infer(e.receiver)
            "Any"
        }
        is Expr.Index -> {
            infer(e.receiver)
            infer(e.index)
            "Any"
        }
        is Expr.Assign -> {
            val vt = infer(e.value)
            infer(e.target)
            vt
        }
        is Expr.StringTemplate -> "String"
        Expr.Unknown -> "Any"
    }

    private fun compatible(got: String, expected: String): Boolean {
        if (got == expected) return true
        if (expected == "Any" || got == "Any") return true
        if (expected.endsWith("?") && got.removeSuffix("?") == expected.removeSuffix("?")) return true
        if (got == "Nothing?" && expected.endsWith("?")) return true
        if (expected == "Unit" && got == "Unit") return true
        return false
    }

    private fun push() = scopes.addLast(mutableMapOf())
    private fun pop() = scopes.removeLast()
    private fun current() = scopes.last()
    private fun lookup(name: String): String? {
        for (i in scopes.indices.reversed()) {
            scopes[i][name]?.let { return it }
        }
        return null
    }
}