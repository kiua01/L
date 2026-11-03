# L Compiler Prototype

Early implementation of the L language toolchain (Kotlin).

## Status (0.0.2)

| Component | Status |
|-----------|--------|
| Lexer | Working |
| Parser | Expanded subset (package, import, annotations, fun/val/var, class/object/interface/enum/data/sealed, nested members) |
| Diagnostics | Basic error bag with source positions |
| Name resolver | Symbol table for declarations (redeclaration checks) |
| Type checker | Not started |
| JVM backend | Not started |

## Commands

```bash
./gradlew :compiler:run --args="lex <file>"
./gradlew :compiler:run --args="parse <file>"
./gradlew :compiler:run --args="resolve <file>"
./gradlew :compiler:run --args="check <file>"
./gradlew :compiler:run --args="version"
./gradlew :compiler:test
```

## Layout

```
compiler/src/main/kotlin/l/compiler/
  Token.kt
  Lexer.kt
  Ast.kt
  Parser.kt
  Diagnostic.kt
  Resolver.kt
  Main.kt
```

## Next

1. Richer expression / statement parsing
2. Type checking subset
3. JVM bytecode for simple functions
4. Gradle wrapper in repo