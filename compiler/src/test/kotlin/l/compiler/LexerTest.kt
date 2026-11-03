package l.compiler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun multipleFunctions() {
        val source = """
            fun add(a: Int, b: Int): Int {
                return a + b
            }
            fun main() {
                println(add(2, 3))
            }
        """.trimIndent()
        val r = CompilerPipeline.analyze(source)
        assertFalse(r.diagnostics.hasErrors)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(r.ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }
}