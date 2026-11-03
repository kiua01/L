# L Compiler Prototype 0.0.3

## Status

| Component | Status |
|-----------|--------|
| Lexer | Working |
| Parser | Expressions + statements + declarations subset |
| Diagnostics | Errors/warnings with positions |
| Resolver | Symbol table |
| Type checker | Literals, locals, simple returns / mismatches |
| JVM backend | Minimal class emitter (`main` → prints greeting) |

## Commands

```bash
./gradlew :compiler:run --args="lex <file>"
./gradlew :compiler:run --args="parse <file>"
./gradlew :compiler:run --args="resolve <file>"
./gradlew :compiler:run --args="check <file>"
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
./gradlew :compiler:test
```

After compile:

```bash
java -cp out Main
# Hello from L!
```

## Pipeline

```
Source .L / .le
  → Lexer
  → Parser (+ diagnostics)
  → Resolver
  → TypeChecker
  → JvmBackend (tiny subset)
```

## Next

- Real codegen from AST (not stub main)
- Control flow (if/when/while)
- Better standard types and calls
- Gradle wrapper in repo