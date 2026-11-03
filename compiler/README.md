# L Compiler Prototype 0.0.10

## Codegen

- Multiple top-level functions → `public static` methods
- Calls between functions (`invokestatic`)
- Int / Boolean / String locals & params
- String `+`, println, if / while / when

## Example

```l
fun greet(name: String) {
    println("Hello, " + name + "!")
}

fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    greet("L")
    println(add(10, 32))
}
```

```bash
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```