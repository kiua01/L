# L Language Roadmap

## Phase 0 — Vision & Design (Current)
- [x] Language vision and goals
- [x] High-level feature set (Kotlin parity + Minecraft focus)
- [x] Formal grammar sketch
- [x] Compiler architecture notes
- [x] Cross-edition portability vision (Bedrock ↔ Java)
- [x] Lume UI language vision
- [ ] Core type system outline (detailed)
- [ ] Minecraft DSL requirements gathering (deeper)
- [ ] Launcher requirements (PC + Android)

## Phase 1 — Specification
- [ ] Complete language specification (syntax + semantics)
- [ ] Standard library design
- [ ] Interop rules with Java & Kotlin
- [ ] Multiplatform target design (JVM primary, Android secondary)
- [ ] Cross-edition capability mapping design
- [ ] Lume language specification (derived from L)
- [ ] Error handling and diagnostics design

## Phase 2 — Compiler Prototype
- [ ] Lexer + Parser
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

## Phase 5 — Launcher Support + Lume
- [ ] `l.launcher` module
- [ ] Android target improvements
- [ ] Lume compiler / frontend prototype
- [ ] Example PC launcher with modern Lume UI
- [ ] Example Android Java Edition launcher with Lume UI

## Phase 6 — Tooling & Ecosystem
- [ ] Language Server Protocol (LSP) for L and Lume
- [ ] Formatter
- [ ] Build system plugin (Gradle)
- [ ] Documentation site
- [ ] Package registry / publishing

## Long-term
- Native backend (optional)
- Advanced performance features for tick loops
- Community-driven Minecraft libraries
- Mature Bedrock ↔ Java porting tooling
- Rich Lume component ecosystem