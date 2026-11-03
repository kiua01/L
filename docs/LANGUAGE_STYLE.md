# L Language Style: Dual English Vocabulary

L accepts **both** normal English keywords and **modern abbreviated English** forms.

They tokenize to the **same** underlying keyword, so you can mix styles in one project (or even one file).

## Core pairs

| Full / normal | Abbreviated | Meaning |
|---------------|-------------|---------|
| `fun` / `function` | `fn` / `def` | Function |
| `val` | `let` | Immutable binding |
| `var` | `mut` | Mutable binding |
| `return` | `ret` | Return |
| `while` | `loop` | Loop |
| `when` | `match` | Multi-branch |
| `package` | `pkg` | Package |
| `import` | `imp` | Import |
| `class` | `cls` | Class |
| `object` | `obj` | Object |
| `interface` | `iface` | Interface |
| `public` | `pub` | Public |
| `private` | `priv` | Private |
| `protected` | `prot` | Protected |
| `abstract` | `abs` | Abstract |
| `override` | `ovrd` | Override |
| `suspend` | `sus` | Suspend |

## Minecraft domain

| Full | Abbreviated |
|------|-------------|
| `registry` | `reg` |
| `packet` | `pkt` |
| `entity` | `ent` |

## Examples (equivalent)

Normal English:

```l
fun add(a: Int, b: Int): Int {
    return a + b
}
```

Modern abbreviated:

```l
fn add(a: Int, b: Int): Int {
    ret a + b
}
```

Mixed:

```l
fn greet(name: String) {
    println("Hi " + name)
}

fun main() {
    greet("L")
}
```

## Design note

- Prefer **one style per file** for readability, but the compiler does not require it.
- Abbreviations are first-class, not preprocessor macros.
- More aliases can be added without breaking existing code.