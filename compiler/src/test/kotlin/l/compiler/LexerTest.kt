package l.compiler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun stringAndBooleanLocals() {
        val source = """
            fun main() {
                val flag = true
                val msg = "hi"
                if (flag) {
                    println(msg)
                }
            }
        """.trimIndent()
        val r = CompilerPipeline.analyze(source)
        assertFalse(r.diagnostics.hasErrors)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(r.ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }

    @Test
    fun whenStillWorks() {
        val source = """
            fun main() {
                when (2) {
                    1 -> println("one")
                    2 -> println("two")
                    else -> println("other")
                }
            }
        """.trimIndent()
        val r = CompilerPipeline.analyze(source)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(r.ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
    }
}