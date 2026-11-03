package l.compiler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LexerTest {
    @Test
    fun tokenizesSimpleFunction() {
        val source = """
            fun main() {
                val x = 42
            }
        """.trimIndent()
        val tokens = Lexer(source).tokenize().filter { it.type != TokenType.EOF }
        assertTrue(tokens.any { it.type == TokenType.FUN })
        assertTrue(tokens.any { it.type == TokenType.VAL })
        assertTrue(tokens.any { it.type == TokenType.INT && it.lexeme == "42" })
    }

    @Test
    fun tokenizesStringAndKeywords() {
        val source = """registry<Block> { "cool" { } }"""
        val tokens = Lexer(source).tokenize()
        assertTrue(tokens.any { it.type == TokenType.REGISTRY })
        assertTrue(tokens.any { it.type == TokenType.STRING && it.lexeme == "cool" })
    }

    @Test
    fun parsesPackageAndFun() {
        val source = """
            package demo.mod
            fun hello(name: String) {}
        """.trimIndent()
        val ast = Parser(Lexer(source).tokenize()).parseFile()
        assertEquals("demo.mod", ast.packageName)
        assertTrue(ast.declarations.any { it is Decl.Function && it.name == "hello" })
    }
}