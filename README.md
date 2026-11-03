# L

**L** is a modern programming language purpose-built for the Minecraft ecosystem.

It is designed from the ground up for:
- Minecraft Java Edition modding (Fabric, Forge, NeoForge, Quilt, etc.)
- High-performance Minecraft launchers (PC + Android)
- **Bidirectional porting** between Bedrock Add-ons and Java Edition Mods
- Modern, beautiful launcher and tool user interfaces
- Cross-platform game tooling
- Seamless interop with existing Java/Kotlin Minecraft codebases

L has **full feature parity with Kotlin** while adding Minecraft-first ergonomics and powerful new capabilities.

**One language. One extension (`.L`). Everything.**

> Status: **Design / Early Specification** stage.

---

## Major Features

### 1. Minecraft Java Modding & Launchers
Write mods and launchers with modern language features, excellent concurrency, and first-class Android support for Java Edition launchers.

### 2. Cross-Edition Portability
**Port any Bedrock Add-on to a Java Edition mod**  
**Port any Java Edition mod to a Bedrock Add-on**

L provides a unified high-level model that can target both ecosystems, with clear diagnostics when features differ.

### 3. Modern UI (Built into L)
Create very modern, beautiful, fluid interfaces for launchers and tools — all in the same language.

- Declarative & reactive
- Excellent desktop + Android support
- Deep integration with the rest of L
- No separate UI language needed

### 4. Single File Extension
All L source files use **`.L`**:

```
Main.L              // mod
RobotEntity.L       // cross-edition entity
HomeScreen.L        // modern launcher UI
LauncherCore.L      // shared launcher logic
```

---

## Quick Links

| Document | Description |
|----------|-------------|
| [Goals](docs/GOALS.md) | Primary goals and non-goals |
| [Features](docs/FEATURES.md) | Cross-edition portability + modern UI |
| [Specification](docs/SPEC.md) | Early language specification draft |
| [Syntax](docs/SYNTAX.md) | Kotlin parity + Minecraft DSL vision |
| [Grammar](docs/GRAMMAR.md) | Formal grammar sketch |
| [Compiler](docs/COMPILER.md) | Compiler architecture notes |
| [Roadmap](ROADMAP.md) | Development phases |
| [Contributing](CONTRIBUTING.md) | How to contribute |

---

## Why L?

| Goal | How L helps |
|------|-------------|
| Kotlin familiarity | 100% Kotlin syntax & feature set as baseline |
| Minecraft modding | Built-in DSL support for registries, events, data generation, networking |
| Launchers | Excellent concurrency + modern UI capabilities |
| Android Java Edition launchers | First-class Android target |
| Bedrock ↔ Java porting | Unified model that can target both editions |
| Modern UI | Declarative UI built directly into the language |
| One language | Everything uses `.L` |

---

## Core Design Principles

1. **Kotlin-first** — Full feature parity with Kotlin  
2. **Minecraft-native** — Registries, events, networking, datagen DSLs  
3. **Cross-edition** — One model, multiple targets (Java + Bedrock)  
4. **Modern UI** — Beautiful interfaces written in the same language  
5. **Performance** — Low-allocation APIs for game loops and launchers  
6. **Multiplatform** — JVM + Android (+ future Native)  
7. **Excellent interop** — Seamless with existing Java/Kotlin libraries  
8. **One language, one extension** — `.L` for everything  

---

## Examples

All examples use the **`.L`** extension:

- [`simple-mod`](examples/simple-mod/) — Basic Fabric-style mod
- [`launcher-core`](examples/launcher-core/) — Shared launcher logic (PC + Android)
- [`launcher-ui`](examples/launcher-ui/) — Modern launcher UI
- [`cross-edition`](examples/cross-edition/) — Entity that targets Java + Bedrock

---

## Project Structure

```
L/
├── README.md
├── ROADMAP.md
├── CONTRIBUTING.md
├── LICENSE
├── docs/
│   ├── GOALS.md
│   ├── FEATURES.md
│   ├── SPEC.md
│   ├── SYNTAX.md
│   ├── GRAMMAR.md
│   └── COMPILER.md
└── examples/
    ├── simple-mod/
    ├── launcher-core/
    ├── launcher-ui/
    └── cross-edition/
```

---

## Current Status

- [x] Language vision & goals
- [x] High-level specification draft
- [x] Syntax overview + Minecraft DSL vision
- [x] Formal grammar sketch
- [x] Compiler architecture notes
- [x] Cross-edition portability vision
- [x] Modern UI capabilities (built into L)
- [x] Single `.L` file extension decision
- [x] Roadmap
- [x] Example sketches
- [ ] Detailed type system outline
- [ ] Standard library design
- [ ] Compiler prototype
- [ ] Minecraft DSL prototypes
- [ ] UI module prototype
- [ ] Tooling (LSP, formatter, build plugin)

---

## Contributing

This project is in very early stages. Feedback on the language design, Minecraft ergonomics, cross-edition porting, and modern UI needs is highly welcome.

See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

MIT License — see [LICENSE](LICENSE).

---

**L** — One language for the entire Minecraft creator and launcher ecosystem.  
**Everything ends with `.L`**