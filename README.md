# L

**L** is a modern programming language purpose-built for the Minecraft ecosystem.

It is designed from the ground up for:
- Minecraft Java Edition modding (Fabric, Forge, NeoForge, Quilt, etc.)
- High-performance Minecraft launchers (PC + Android)
- Cross-platform game tooling and launchers that need to run on desktop and mobile
- Seamless interop with existing Java/Kotlin Minecraft codebases

L has **full feature parity with Kotlin** while adding Minecraft-first ergonomics, better performance characteristics for game loops, and first-class support for Android launchers of Java Edition.

> Status: **Design / Early Specification** stage.  
> Compiler, standard library, and tooling are under active design.

---

## Quick Links

| Document | Description |
|----------|-------------|
| [Goals](docs/GOALS.md) | Primary goals and non-goals |
| [Specification](docs/SPEC.md) | Early language specification draft |
| [Syntax](docs/SYNTAX.md) | Kotlin parity + Minecraft DSL vision |
| [Roadmap](ROADMAP.md) | Development phases |
| [Contributing](CONTRIBUTING.md) | How to contribute |

---

## Why L?

Minecraft modding and launcher development currently live in a mix of Java, Kotlin, and sometimes Groovy. L aims to be the dedicated language for this domain:

| Goal | How L helps |
|------|-------------|
| Kotlin familiarity | 100% Kotlin syntax & feature set as baseline |
| Minecraft modding | Built-in DSL support for registries, events, data generation, networking |
| Launchers | Excellent concurrency, low GC pressure, easy packaging for PC + Android |
| Android Java Edition launchers | First-class Android target + easy JNI / native interop when needed |
| Modern language | Null safety, coroutines, multiplatform, sealed types, context receivers, etc. |

---

## Core Design Principles

1. **Kotlin-first**  
   Every Kotlin feature (including the latest stable ones) is supported. L code should feel instantly familiar to Kotlin developers.

2. **Minecraft-native**  
   Language constructs and standard library modules tailored for:
   - Block / Item / Entity registration
   - Event buses
   - Networking (C2S / S2C packets)
   - Data generation
   - Resource management
   - Mixin / access widener ergonomics

3. **Performance for games**  
   - Value classes / inline classes by default where useful
   - Explicit memory layouts for hot paths
   - Low-allocation APIs for tick loops and rendering

4. **True multiplatform**  
   - JVM (primary for Minecraft)
   - Android (for Java Edition launchers on mobile)
   - Native (future) for performance-critical launcher components

5. **Excellent interop**  
   Seamless calling of existing Java and Kotlin Minecraft libraries. L modules can be published as regular `.jar`s.

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
│   ├── SPEC.md
│   └── SYNTAX.md
└── examples/
    ├── simple-mod/
    └── launcher-core/
```

---

## Current Status

- [x] Language vision & goals
- [x] High-level specification draft
- [x] Syntax overview + Minecraft DSL vision
- [x] Roadmap
- [x] Example sketches
- [ ] Formal grammar
- [ ] Core type system specification
- [ ] Compiler prototype
- [ ] Standard library design
- [ ] Minecraft DSL prototypes
- [ ] Tooling (LSP, formatter, build plugin)

---

## Contributing

This project is in very early stages. Feedback on the language design, Minecraft ergonomics, and launcher needs is highly welcome.

See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

MIT License — see [LICENSE](LICENSE).

---

**L** — Because Minecraft deserves a language that was designed for it.