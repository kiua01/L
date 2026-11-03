# L Compiler Prototype 0.0.11

## Dual vocabulary

L accepts **normal English** and **modern abbreviated English** keywords:

| Normal | Abbreviated |
|--------|-------------|
| `fun` / `function` | `fn` / `def` |
| `val` | `let` |
| `var` | `mut` |
| `return` | `ret` |
| `while` | `loop` |
| `when` | `match` |
| `package` | `pkg` |
| `import` | `imp` |
| `class` | `cls` |
| `object` | `obj` |

See [docs/LANGUAGE_STYLE.md](../docs/LANGUAGE_STYLE.md).

## Codegen

- Multiple functions + calls
- Int / Boolean / String
- String `+`, println, if / while / when

```bash
./gradlew :compiler:run --args="compile examples/hello/HelloAbbreviated.L out Main"
java -cp out Main
```