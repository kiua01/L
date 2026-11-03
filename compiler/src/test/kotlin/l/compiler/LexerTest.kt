package l.compiler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun tokenizesSimpleFunction() {
        val tokens = Lexer("fun main() { val x = 42 }").tokenize()
        assertTrue(tokens.any { it.type == TokenType.FUN })
        assertTrue(tokens.any { it.type == TokenType.INT && it.lexeme == "42" })
    }

    @Test
    fun parsesIfWhile() {
        val source = """
            fun main() {
                if (true) {
                    println("yes")
                } else {
                    println("no")
                }
                while (false) {
                    println("loop")
                }
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val fn = ast.declarations.filterIsInstance<Decl.Function>().first()
        val body = fn.body as Body.Block
        assertTrue(body.statements.any { it is Stmt.If })
        assertTrue(body.statements.any { it is Stmt.While })
        assertFalse(diag.hasErrors)
    }

    @Test
    fun typechecksPrintln() {
        val source = """
            fun main() {
                println("hi")
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        TypeChecker(diag).check(ast)
        assertFalse(diag.hasErrors)
    }

    @Test
    fun collectPrintlnsFromAst() {
        val source = """
            fun main() {
                println("A")
                println("B")
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        // compile should not throw
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }
}