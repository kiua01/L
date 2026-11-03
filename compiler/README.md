# L Compiler Prototype 0.0.6

## Status

| Component | Status |
|-----------|--------|
| Lexer / Parser | `if`, `while`, **`when`** |
| Type checker | Subset including `when` |
| JVM backend | println, Int locals, arithmetic, if/while, **when** |

## Example

```l
fun main() {
    val x = 10
    when (x) {
        10 -> println("x is 10")
        20 -> println("x is 20")
        else -> println("other")
    }
}
```

```bash
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```

## Next

- StackMapTable / classfile improvements
- Boolean locals
- Gradle wrapper