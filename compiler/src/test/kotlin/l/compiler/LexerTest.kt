package l.compiler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun parsesWhen() {
        val source = """
            fun main() {
                when (1) {
                    1 -> println("one")
                    else -> println("other")
                }
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val fn = ast.declarations.filterIsInstance<Decl.Function>().first()
        val body = fn.body as Body.Block
        assertTrue(body.statements.any { it is Stmt.When })
        assertFalse(diag.hasErrors)
    }

    @Test
    fun compilesWhen() {
        val source = """
            fun main() {
                when (2) {
                    1 -> println("one")
                    2 -> println("two")
                    else -> println("other")
                }
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        TypeChecker(diag).check(ast)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }
}