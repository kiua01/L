# L Compiler Prototype

Early implementation of the L language toolchain.

## Status

| Component | Status |
|-----------|--------|
| Lexer | Working (Kotlin-like + L soft keywords) |
| Parser | Minimal (package, fun, val/var, class/object) |
| Type checker | Not started |
| JVM backend | Not started |
| `.le` web specifics | Recognized as source files only |

## Build

Requires JDK 17+ and Gradle.

```bash
./gradlew :compiler:run --args="version"
./gradlew :compiler:run --args="lex ../examples/simple-mod/Main.L"
./gradlew :compiler:run --args="parse ../examples/simple-mod/Main.L"
./gradlew :compiler:test
```

If the Gradle wrapper is not present yet:

```bash
gradle wrapper
```

## Layout

```
compiler/
  src/main/kotlin/l/compiler/
    Token.kt
    Lexer.kt
    Ast.kt
    Parser.kt
    Main.kt
  src/test/kotlin/l/compiler/
    LexerTest.kt
```

## Next steps

1. Expand parser toward the grammar in `docs/GRAMMAR.md`
2. Name resolution + basic type checking
3. JVM bytecode emission for a tiny subset
4. Better diagnostics with source ranges