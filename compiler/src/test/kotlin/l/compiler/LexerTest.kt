package l.compiler

import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun parsesExpressions() {
        val source = """
            fun add(a: Int, b: Int): Int = a + b * 2
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val fn = ast.declarations.filterIsInstance<Decl.Function>().first()
        assertTrue(fn.body is Body.Expression)
        assertFalse(diag.hasErrors)
    }

    @Test
    fun typechecksLiterals() {
        val source = """
            fun f(): Int {
                val x: Int = 1
                return x
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        TypeChecker(diag).check(ast)
        assertFalse(diag.hasErrors)
    }

    @Test
    fun typecheckMismatch() {
        val source = """
            fun f(): Int {
                val x: Int = "nope"
                return x
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        TypeChecker(diag).check(ast)
        assertTrue(diag.hasErrors)
    }

    @Test
    fun resolverNested() {
        val source = "package demo\nobject M { fun f() {} }"
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val symbols = Resolver(diag).resolve(ast)
        assertTrue(symbols.any { it.name == "f" })
    }
}