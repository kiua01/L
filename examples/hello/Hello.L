// examples/hello/Hello.L

fun main() {
    println("Hello from L!")
    val x = 10
    val y = 32
    println(x + y)
    if (x < y) {
        println("x < y")
    } else {
        println("x >= y")
    }
    when (x) {
        10 -> println("x is 10")
        20 -> println("x is 20")
        else -> println("other")
    }
}