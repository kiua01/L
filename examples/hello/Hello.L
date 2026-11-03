// examples/hello/Hello.L

fun main() {
    println("Hello from L!")
    val x = 10
    val y = 32
    println(x + y)

    val ok = true
    if (ok) {
        println("boolean ok")
    }

    val name = "L"
    val greeting = "Hello, " + name + "!"
    println(greeting)

    when (x) {
        10 -> println("x is 10")
        else -> println("other")
    }
}