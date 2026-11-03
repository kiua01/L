// examples/hello/Hello.L

fun greet(name: String) {
    println("Hello, " + name + "!")
}

fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    greet("L")
    val sum = add(10, 32)
    println(sum)
}