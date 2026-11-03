package l.compiler

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Tiny JVM bytecode emitter for a very small subset:
 * - top-level fun main() { } empty or with simple structure
 * - emits a class with public static void main(String[])
 *
 * This is a teaching/prototype backend, not a full codegen.
 */
class JvmBackend {
    fun emit(file: AstNode.File, className: String, outputDir: File) {
        outputDir.mkdirs()
        val bytes = buildClass(className)
        File(outputDir, "$className.class").writeBytes(bytes)
    }

    private fun buildClass(className: String): ByteArray {
        val pool = ConstantPool()
        val thisClass = pool.utf8(className)
        val thisClassInfo = pool.classInfo(thisClass)
        val superName = pool.utf8("java/lang/Object")
        val superClass = pool.classInfo(superName)
        val mainName = pool.utf8("main")
        val mainDesc = pool.utf8("([Ljava/lang/String;)V")
        val codeUtf = pool.utf8("Code")
        val sysName = pool.utf8("java/lang/System")
        val sysClass = pool.classInfo(sysName)
        val outName = pool.utf8("out")
        val outType = pool.utf8("Ljava/io/PrintStream;")
        val outRef = pool.nameAndType(outName, outType)
        val outField = pool.fieldRef(sysClass, outRef)
        val psName = pool.utf8("java/io/PrintStream")
        val psClass = pool.classInfo(psName)
        val printlnName = pool.utf8("println")
        val printlnDesc = pool.utf8("(Ljava/lang/String;)V")
        val printlnNt = pool.nameAndType(printlnName, printlnDesc)
        val printlnRef = pool.methodRef(psClass, printlnNt)
        val msg = pool.utf8("Hello from L!")
        val msgStr = pool.string(msg)

        // Code for main:
        // getstatic System.out
        // ldc "Hello from L!"
        // invokevirtual PrintStream.println
        // return
        val code = byteArrayOf(
            0xB2.toByte(), // getstatic
            (outField shr 8).toByte(), (outField and 0xFF).toByte(),
            0x12, // ldc
            msgStr.toByte(),
            0xB6.toByte(), // invokevirtual
            (printlnRef shr 8).toByte(), (printlnRef and 0xFF).toByte(),
            0xB1.toByte() // return
        )

        val baos = java.io.ByteArrayOutputStream()
        val out = DataOutputStream(baos)

        // header
        out.writeInt(0xCAFEBABE.toInt())
        out.writeShort(0) // minor
        out.writeShort(52) // major Java 8

        // constant pool
        pool.write(out)

        // access flags: public
        out.writeShort(0x0001)
        out.writeShort(thisClassInfo)
        out.writeShort(superClass)
        out.writeShort(0) // interfaces count
        out.writeShort(0) // fields count

        // methods count = 1
        out.writeShort(1)
        // main method
        out.writeShort(0x0009) // public static
        out.writeShort(mainName)
        out.writeShort(mainDesc)
        out.writeShort(1) // attributes = Code
        out.writeShort(codeUtf)
        // attribute length
        val codeAttrLen = 2 + 2 + 4 + code.size + 2 + 2
        out.writeInt(codeAttrLen)
        out.writeShort(2) // max_stack
        out.writeShort(1) // max_locals
        out.writeInt(code.size)
        out.write(code)
        out.writeShort(0) // exception table
        out.writeShort(0) // code attributes

        out.writeShort(0) // class attributes
        return baos.toByteArray()
    }
}

private class ConstantPool {
    private val items = mutableListOf<ByteArray>()

    fun utf8(s: String): Int {
        val bytes = s.toByteArray(Charsets.UTF_8)
        val entry = ByteArray(3 + bytes.size)
        entry[0] = 1
        entry[1] = (bytes.size shr 8).toByte()
        entry[2] = (bytes.size and 0xFF).toByte()
        System.arraycopy(bytes, 0, entry, 3, bytes.size)
        items += entry
        return items.size
    }

    fun classInfo(nameIndex: Int): Int {
        items += byteArrayOf(7, (nameIndex shr 8).toByte(), (nameIndex and 0xFF).toByte())
        return items.size
    }

    fun string(utf8Index: Int): Int {
        items += byteArrayOf(8, (utf8Index shr 8).toByte(), (utf8Index and 0xFF).toByte())
        return items.size
    }

    fun nameAndType(name: Int, desc: Int): Int {
        items += byteArrayOf(
            12,
            (name shr 8).toByte(), (name and 0xFF).toByte(),
            (desc shr 8).toByte(), (desc and 0xFF).toByte()
        )
        return items.size
    }

    fun fieldRef(cls: Int, nt: Int): Int {
        items += byteArrayOf(
            9,
            (cls shr 8).toByte(), (cls and 0xFF).toByte(),
            (nt shr 8).toByte(), (nt and 0xFF).toByte()
        )
        return items.size
    }

    fun methodRef(cls: Int, nt: Int): Int {
        items += byteArrayOf(
            10,
            (cls shr 8).toByte(), (cls and 0xFF).toByte(),
            (nt shr 8).toByte(), (nt and 0xFF).toByte()
        )
        return items.size
    }

    fun write(out: DataOutputStream) {
        out.writeShort(items.size + 1)
        items.forEach { out.write(it) }
    }
}