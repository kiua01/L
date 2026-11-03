// examples/hello/HelloAbbreviated.L
// Same program using modern abbreviated English keywords

fn greet(name: String) {
    println("Hello, " + name + "!")
}

fn add(a: Int, b: Int): Int {
    ret a + b
}

fn main() {
    greet("L")
    let sum = add(10, 32)
    println(sum)

    mut i = 0
    loop (i < 3) {
        println(i)
        i = i + 1
    }

    match (sum) {
        42 -> println("forty-two")
        else -> println("other")
    }
}