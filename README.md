# L

**L** is a modern programming language purpose-built for the Minecraft ecosystem — and beyond.

It is designed for:
- Minecraft Java Edition modding (Fabric, Forge, NeoForge, Quilt, etc.)
- High-performance Minecraft launchers (PC + Android)
- **Bidirectional porting** between Bedrock Add-ons and Java Edition Mods
- Modern, beautiful launcher and tool user interfaces
- **First-class 3D** (tools, viewers, games, web)
- **Websites and 3D websites**
- Seamless interop with existing Java/Kotlin codebases

L has **full feature parity with Kotlin** while adding powerful domain capabilities.

**One language. Two extensions:**
- **`.L`** — mods, launchers, logic, desktop/mobile UI
- **`.le`** — websites and web 3D

> Status: **Design / Early Specification** stage.

---

## Major Features

| Feature | Description |
|---------|-------------|
| Java Modding & Launchers | Modern language + strong concurrency + Android support |
| Cross-Edition Portability | Bedrock Add-on ↔ Java Mod |
| Modern UI | Declarative UI built into L |
| First-class 3D | Scenes, models, cameras, lights, animation |
| Websites & 3D Websites | Modern web + real-time 3D on the web (`.le`) |

---

## Quick Links

| Document | Description |
|----------|-------------|
| [Goals](docs/GOALS.md) | Primary goals and non-goals |
| [Features](docs/FEATURES.md) | All major capabilities |
| [3D & Web](docs/WEB_AND_3D.md) | 3D, websites, 3D websites, `.le` extension |
| [Specification](docs/SPEC.md) | Early language specification draft |
| [Type System](docs/TYPE_SYSTEM.md) | Type system outline |
| [Standard Library](docs/STDLIB.md) | Core, Minecraft, Launcher, UI, Graphics modules |
| [Minecraft DSL](docs/MINECRAFT_DSL.md) | Registries, entities, events, packets, datagen |
| [UI Module](docs/UI.md) | Declarative UI, state, layout, animation |
| [Syntax](docs/SYNTAX.md) | Kotlin parity + DSLs |
| [Grammar](docs/GRAMMAR.md) | Formal grammar sketch |
| [Compiler](docs/COMPILER.md) | Compiler architecture notes |
| [Roadmap](ROADMAP.md) | Development phases |
| [Contributing](CONTRIBUTING.md) | How to contribute |

---

## File Extensions

| Extension | Used for |
|-----------|----------|
| `.L` | Mods, launchers, general logic, desktop/mobile UI |
| `.le` | Websites and web 3D |

```
Main.L                 // mod or launcher
HomeScreen.L           // desktop / Android UI
RobotEntity.L          // cross-edition entity
HomePage.le            // website page
ProductViewer3D.le     // 3D on the web
```

---

## Examples

- [`simple-mod`](examples/simple-mod/) — Basic mod (`.L`)
- [`launcher-core`](examples/launcher-core/) — Shared launcher logic (`.L`)
- [`launcher-ui`](examples/launcher-ui/) — Modern launcher UI (`.L`)
- [`cross-edition`](examples/cross-edition/) — Java + Bedrock entity (`.L`)
- [`website`](examples/website/) — Website + 3D viewer (`.le`)

---

## Current Status

- [x] Language vision & goals
- [x] Specification, type system, grammar, compiler notes
- [x] Minecraft DSL design
- [x] UI module specification
- [x] Standard library outline
- [x] Cross-edition portability vision
- [x] First-class 3D vision
- [x] Websites & 3D websites vision
- [x] `.L` and `.le` extension decision
- [x] Example sketches
- [ ] Compiler prototype
- [ ] Runtime prototypes (Minecraft, UI, 3D, Web)
- [ ] Tooling (LSP, formatter, build plugin)

---

## License

MIT License — see [LICENSE](LICENSE).

---

**L** — One language for mods, launchers, UI, 3D, websites, and 3D websites.