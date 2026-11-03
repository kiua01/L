# L Compiler Prototype 0.0.5

## Status

| Component | Status |
|-----------|--------|
| Lexer / Parser | Working (incl. `if` / `while`) |
| Type checker | Subset |
| JVM backend | `println`, Int locals, `+ - * /`, comparisons, **real if/while jumps** |

## Example

```l
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
}
```

```bash
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```

## Next

- `when`
- Boolean locals
- Loops with proper stack maps (Java 7+ verifier)
- Gradle wrapper