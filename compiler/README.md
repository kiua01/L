# L Compiler Prototype 0.0.12

## Highlights

- Dual vocabulary (`fn`/`ret`/`let`/`obj` …)
- Multiple functions + calls
- **`object` / `class` members → separate `.class` files**
- Minecraft DSL analysis stub (registries / entities / packets detection)
- String concat, println, control flow

## Object example

```l
obj Util {
    fn double(x: Int): Int {
        ret x + x
    }
}

fn main() {
    println("ok")
}
```

Produces `Main.class` and `Util.class`.

```bash
./gradlew :compiler:run --args="compile examples/hello/Objects.L out Main"
```