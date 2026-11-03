package l.compiler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun abbreviatedKeywords() {
        val source = """
            fn add(a: Int, b: Int): Int {
                ret a + b
            }
            fn main() {
                let x = add(1, 2)
                println(x)
            }
        """.trimIndent()
        val tokens = Lexer(source).tokenize()
        assertTrue(tokens.any { it.type == TokenType.FUN && it.lexeme == "fn" })
        assertTrue(tokens.any { it.type == TokenType.RETURN && it.lexeme == "ret" })
        assertTrue(tokens.any { it.type == TokenType.VAL && it.lexeme == "let" })
        val r = CompilerPipeline.analyze(source)
        assertFalse(r.diagnostics.hasErrors)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(r.ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }
}