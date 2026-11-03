# L

**L** is a modern programming language purpose-built for the Minecraft ecosystem.

It is designed from the ground up for:
- Minecraft Java Edition modding (Fabric, Forge, NeoForge, Quilt, etc.)
- High-performance Minecraft launchers (PC + Android)
- **Bidirectional porting** between Bedrock Add-ons and Java Edition Mods
- Cross-platform game tooling
- Seamless interop with existing Java/Kotlin Minecraft codebases

L has **full feature parity with Kotlin** while adding Minecraft-first ergonomics and powerful new capabilities.

> Status: **Design / Early Specification** stage.

---

## Major Features

### 1. Minecraft Java Modding & Launchers
Write mods and launchers with modern language features, excellent concurrency, and first-class Android support for Java Edition launchers.

### 2. Cross-Edition Portability
**Port any Bedrock Add-on to a Java Edition mod**  
**Port any Java Edition mod to a Bedrock Add-on**

L provides a unified high-level model that can target both ecosystems, with clear diagnostics when features differ.

### 3. Lume — Modern UI Language
A **derived language** (working name: **Lume**) specialized for building beautiful, modern launcher and tool interfaces.

- Declarative & reactive
- Excellent desktop + Android support
- Deep integration with L backend logic
- Designed for very modern, fluid UIs

---

## Quick Links

| Document | Description |
|----------|-------------|
| [Goals](docs/GOALS.md) | Primary goals and non-goals |
| [Features](docs/FEATURES.md) | Cross-edition portability + Lume UI language |
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
| Launchers | Excellent concurrency + **Lume** for modern UIs |
| Android Java Edition launchers | First-class Android target |
| Bedrock ↔ Java porting | Unified model that can target both editions |
| Modern language | Null safety, coroutines, multiplatform, sealed types, context receivers, etc. |

---

## Core Design Principles

1. **Kotlin-first** — Full feature parity with Kotlin  
2. **Minecraft-native** — Registries, events, networking, datagen DSLs  
3. **Cross-edition** — One model, multiple targets (Java + Bedrock)  
4. **Modern UI** — Lume for beautiful launcher interfaces  
5. **Performance** — Low-allocation APIs for game loops and launchers  
6. **Multiplatform** — JVM + Android (+ future Native)  
7. **Excellent interop** — Seamless with existing Java/Kotlin libraries  

---

## Examples

See the [`examples/`](examples/) directory:

- [`simple-mod`](examples/simple-mod/) — Vision of a basic Fabric-style mod
- [`launcher-core`](examples/launcher-core/) — Shared launcher logic for PC + Android

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
│   ├── FEATURES.md      ← Cross-edition + Lume
│   ├── SPEC.md
│   ├── SYNTAX.md
│   ├── GRAMMAR.md
│   └── COMPILER.md
└── examples/
    ├── simple-mod/
    └── launcher-core/
```

---

## Current Status

- [x] Language vision & goals
- [x] High-level specification draft
- [x] Syntax overview + Minecraft DSL vision
- [x] Formal grammar sketch
- [x] Compiler architecture notes
- [x] Cross-edition portability vision
- [x] Lume UI language vision
- [x] Roadmap
- [x] Example sketches
- [ ] Detailed type system outline
- [ ] Standard library design
- [ ] Compiler prototype
- [ ] Minecraft DSL prototypes
- [ ] Lume prototype
- [ ] Tooling (LSP, formatter, build plugin)

---

## Contributing

This project is in very early stages. Feedback on the language design, Minecraft ergonomics, cross-edition porting, and modern UI needs is highly welcome.

See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

MIT License — see [LICENSE](LICENSE).

---

**L** — One language family for the entire Minecraft creator and launcher ecosystem.