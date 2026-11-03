# L Language Roadmap

## Phase 0 — Vision & Design (Current)
- [x] Language vision and goals
- [x] High-level feature set (Kotlin parity + Minecraft focus)
- [x] Formal grammar sketch
- [x] Compiler architecture notes
- [x] Type system outline
- [x] Standard library outline
- [x] Minecraft DSL design (registries, entities, events, packets, datagen)
- [x] UI module specification
- [x] Cross-edition portability vision (Bedrock ↔ Java)
- [x] Modern UI capabilities (built into L)
- [x] Single language + `.L` file extension decision
- [ ] Cross-edition capability mapping (detailed)
- [ ] Launcher requirements (deeper)

## Phase 1 — Specification
- [ ] Complete language specification (syntax + semantics)
- [ ] Interop rules with Java & Kotlin
- [ ] Multiplatform target design (JVM primary, Android secondary)
- [ ] Cross-edition capability mapping design
- [ ] Error handling and diagnostics design

## Phase 2 — Compiler Prototype
- [ ] Lexer + Parser (`.L` files)
- [ ] Name resolution & type checking (subset)
- [ ] JVM bytecode backend (basic)
- [ ] Simple standard library subset
- [ ] Basic CLI (`l` command)

## Phase 3 — Minecraft Modules
- [ ] `l.minecraft` core
- [ ] Registry DSL
- [ ] Event system helpers
- [ ] Networking / packet DSL
- [ ] Data generation support
- [ ] Example Fabric / NeoForge mods

## Phase 4 — Cross-Edition & Portability
- [ ] Unified entity / block / item model
- [ ] Bedrock backend prototype
- [ ] Capability mapping & diagnostics
- [ ] First successful bidirectional port examples

## Phase 5 — Launcher Support + Modern UI
- [ ] `l.launcher` module
- [ ] Android target improvements
- [ ] UI module prototype (declarative, reactive)
- [ ] Example PC launcher with modern L UI
- [ ] Example Android Java Edition launcher with modern L UI

## Phase 6 — Tooling & Ecosystem
- [ ] Language Server Protocol (LSP)
- [ ] Formatter
- [ ] Build system plugin (Gradle)
- [ ] Documentation site
- [ ] Package registry / publishing

## Long-term
- Native backend (optional)
- Advanced performance features for tick loops
- Community-driven Minecraft libraries
- Mature Bedrock ↔ Java porting tooling
- Rich UI component ecosystem