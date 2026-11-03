# L Compiler Prototype 0.0.9

## Codegen

- Int / Boolean / String locals
- Arithmetic, comparisons, `&&` `||` `!`
- **String `+` concatenation** (StringBuilder)
- `println`
- `if` / `while` / `when`

## Run

```bash
./gradlew :compiler:test
./gradlew :compiler:run --args="compile examples/hello/Hello.L out Main"
java -cp out Main
```

## Example

```l
val name = "L"
val greeting = "Hello, " + name + "!"
println(greeting)
```