# L Compiler Prototype 0.0.13

## New

- **Static fields** on `object` / `class` (`let`/`mut` / `val`/`var`)
- Field init via `<clinit>`
- `getstatic` / `putstatic` for field access inside methods

## Example

```l
obj Counter {
    mut count = 0
    fn bump(): Int {
        count = count + 1
        ret count
    }
}
```

```bash
./gradlew :compiler:run --args="compile examples/hello/Fields.L out Main"
```

Also: dual vocabulary, multi-function, string `+`, Minecraft DSL stub.