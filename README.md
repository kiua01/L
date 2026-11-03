# L

**L** is a modern programming language purpose-built for the Minecraft ecosystem — and beyond.

### What L is for
- Minecraft Java Edition modding (Fabric, Forge, NeoForge, Quilt, …)
- Minecraft launchers (PC + Android)
- Bedrock Add-on ↔ Java Mod portability
- Modern desktop & mobile UI
- Real-time **3D**
- **Websites** and **3D websites**

Full **Kotlin feature parity**, plus domain power.

### File extensions
| Extension | Use |
|-----------|-----|
| **`.L`** | Mods, launchers, logic, desktop/mobile UI, tools |
| **`.le`** | Websites and web 3D |

> **Design phase: complete.**  
> **Compiler prototype: started** (lexer + minimal parser + CLI).

---

## Compiler prototype

```bash
cd compiler
# with Gradle:
./gradlew run --args="lex ../examples/simple-mod/Main.L"
./gradlew run --args="parse ../examples/simple-mod/Main.L"
./gradlew test
```

See [compiler/README.md](compiler/README.md).

---

## Documentation

| Doc | Description |
|-----|-------------|
| [Overview](docs/OVERVIEW.md) | Start here — FAQ & reading order |
| [Goals](docs/GOALS.md) | Goals and non-goals |
| [Features](docs/FEATURES.md) | Major capabilities |
| [Specification](docs/SPEC.md) | Language spec draft |
| [Type System](docs/TYPE_SYSTEM.md) | Types, null safety, domain types |
| [Grammar](docs/GRAMMAR.md) | Formal grammar sketch |
| [Syntax](docs/SYNTAX.md) | Syntax & DSL examples |
| [Standard Library](docs/STDLIB.md) | Core, Minecraft, UI, 3D, web modules |
| [Minecraft DSL](docs/MINECRAFT_DSL.md) | Registries, entities, events, packets, datagen |
| [Cross-Edition](docs/CROSS_EDITION.md) | Bedrock ↔ Java capability mapping |
| [UI](docs/UI.md) | Declarative UI module |
| [3D API](docs/API_3D.md) | 3D API surface |
| [Web & 3D](docs/WEB_AND_3D.md) | Websites, 3D websites, `.le` |
| [Targets](docs/TARGETS.md) | jvm / android / web targets |
| [Interop](docs/INTEROP.md) | Java & Kotlin interop |
| [Diagnostics](docs/DIAGNOSTICS.md) | Errors, warnings, domain diagnostics |
| [Compiler](docs/COMPILER.md) | Compiler architecture |
| [Tooling](docs/TOOLING.md) | CLI, LSP, formatter, build |
| [Packaging](docs/PACKAGING.md) | Jars, Android, web distribution |
| [Roadmap](ROADMAP.md) | Implementation phases |
| [Contributing](CONTRIBUTING.md) | How to contribute |

---

## Examples

```
examples/
├── simple-mod/Main.L
├── launcher-core/LauncherCore.L
├── launcher-ui/HomeScreen.L
├── cross-edition/RobotEntity.L
└── website/
    ├── HomePage.le
    └── ProductViewer3D.le
```

---

## License

MIT — see [LICENSE](LICENSE).

---

**L** — One language for mods, launchers, UI, 3D, websites, and 3D websites.