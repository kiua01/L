# L Compiler Prototype 0.0.7

## Status

| Component | Status |
|-----------|--------|
| Front-end | Lexer, parser (`if`/`while`/`when`), diagnostics, resolver, type checker |
| Pipeline | `CompilerPipeline` shared by CLI |
| JVM backend | println, Int locals, arithmetic, if/while/when |
| Classfile | Java 5 (v49) — runs without StackMapTable |

## Build & run

```bash
chmod +x gradlew
./gradlew :compiler:test
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```

If `./gradlew` is not executable yet:

```bash
gradle wrapper   # optional, regenerates wrapper
gradle :compiler:run --args="version"
```

## Supported surface (codegen)

- `fun main() { ... }`
- `val` / `var` Int locals
- `+ - * /` and comparisons
- `println(String|Int|local)`
- `if` / `else`, `while`, `when` (int subject)

## Next

- More types (Boolean locals, String values)
- StackMapTable + modern class version
- Better errors