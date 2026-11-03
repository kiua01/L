package l.compiler

import kotlin.test.Test
import kotlin.test.assertTrue

class CompilerTests {
    @Test
    fun objectEmitsSeparateClass() {
        val source = """
            obj Util {
                fn double(x: Int): Int {
                    ret x + x
                }
            }
            fn main() {
                println("ok")
            }
        """.trimIndent()
        val r = CompilerPipeline.analyze(source)
        val dir = java.io.File.createTempFile("lout", "").apply { delete(); mkdir() }
        JvmBackend().emit(r.ast, "Main", dir)
        assertTrue(java.io.File(dir, "Main.class").exists())
        assertTrue(java.io.File(dir, "Util.class").exists())
    }
}