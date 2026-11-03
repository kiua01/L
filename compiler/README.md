# L Compiler Prototype 0.0.8

## Codegen support

- `fun main()`
- Int / **Boolean** / **String** locals
- Arithmetic & comparisons on Int
- `&&` / `||` / `!` on Boolean (as 0/1)
- `println` for strings, ints, and locals
- `if` / `while` / `when`

## Run

```bash
./gradlew :compiler:test
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```

## Notes

Classfiles are Java 5 (v49) so StackMapTable is not required.