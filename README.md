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

## Language Features (Kotlin Parity + Extensions)

### Inherited from Kotlin
- Null safety (`?`, `!!`, smart casts)
- Data classes, sealed classes/interfaces
- Coroutines & structured concurrency
- Extension functions
- Delegation
- Inline / value classes
- Context receivers / parameters
- Multiplatform project structure
- Type-safe builders / DSLs
- All standard library collections, sequences, flows, etc.

### L-specific / Minecraft-oriented additions (planned)

```l
// Example vision (not final syntax)

@Mod("mymod")
class MyMod {
    // First-class registry DSL
    val blocks = registry<Block> {
        "cool_block" {
            material = Material.STONE
            hardness = 2.0f
            // ...
        }
    }

    // Event handling with structured concurrency
    on<ServerTickEvent> {
        // low-allocation hot path
    }

    // Easy networking
    packet<MyPacket> {
        // codec + handler
    }
}
```

Other planned ergonomics:
- Compile-time checked resource locations / identifiers
- Built-in support for data generators
- Lightweight entity component system helpers
- Launcher-oriented modules (`l.launcher`, `l.android`, `l.auth`, etc.)

---

## Target Use Cases

1. **Minecraft Mods**  
   Write mods with cleaner, safer, more expressive code while still compiling to normal JVM bytecode that works with Fabric/Forge/etc.

2. **Minecraft Launchers (PC)**  
   Build modern launchers with excellent concurrency, UI (Compose Multiplatform or similar), and easy management of multiple instances / versions.

3. **Minecraft Java Edition Launchers for Android**  
   L is designed so the same core launcher logic can target both desktop JVM and Android, making high-quality Java Edition mobile launchers much more maintainable.

---

## Project Structure (planned)

```
L/
├── spec/                 # Language specification
├── compiler/             # L compiler (front-end + backend)
├── stdlib/               # Standard library
├── minecraft/            # Minecraft-specific standard modules
│   ├── registry/
│   ├── events/
│   ├── networking/
│   └── datagen/
├── launcher/             # Launcher-oriented libraries
├── examples/
│   ├── simple-mod/
│   ├── fabric-mod/
│   └── android-launcher/
└── docs/
```

---

## Current Status

- [x] Language vision & goals
- [ ] Formal grammar
- [ ] Core type system specification
- [ ] Compiler prototype
- [ ] Standard library design
- [ ] Minecraft DSL prototypes
- [ ] Tooling (LSP, formatter, build plugin)

---

## Contributing

This project is in very early stages. Feedback on the language design, Minecraft ergonomics, and launcher needs is highly welcome.

Open issues or discussions for:
- Feature requests related to modding / launchers
- Kotlin feature gaps you care about
- Performance requirements
- Android launcher constraints

---

## License

To be decided (likely Apache 2.0 or similar permissive license).

---

**L** — Because Minecraft deserves a language that was designed for it.