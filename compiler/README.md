# L Compiler Prototype 0.0.4

## Status

| Component | Status |
|-----------|--------|
| Lexer | Working |
| Parser | Declarations + expressions + `if` / `while` |
| Diagnostics | Yes |
| Resolver | Symbol table |
| Type checker | Locals, returns, conditions, `println` |
| JVM backend | AST-driven `println("...")` sequences |

## Commands

```bash
./gradlew :compiler:run --args="check examples/hello/Hello.L"
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
./gradlew :compiler:test
```

## Supported codegen (tiny)

```l
fun main() {
    println("Hello from L!")
    println("Compiler prototype 0.0.4")
}
```

## Next

- Real JVM branching for `if` / `while`
- Local variables in bytecode
- Integer arithmetic codegen
- `when` expression