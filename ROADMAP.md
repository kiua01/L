# L Language Roadmap

## Phase 0 — Vision & Design (Current)
- [x] Language vision and goals
- [x] High-level feature set (Kotlin parity + Minecraft focus)
- [ ] Formal grammar sketch
- [ ] Core type system outline
- [ ] Minecraft DSL requirements gathering
- [ ] Launcher requirements (PC + Android)

## Phase 1 — Specification
- [ ] Complete language specification (syntax + semantics)
- [ ] Standard library design
- [ ] Interop rules with Java & Kotlin
- [ ] Multiplatform target design (JVM primary, Android secondary)
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

## Phase 4 — Launcher Support
- [ ] `l.launcher` module
- [ ] Android target improvements
- [ ] Example PC launcher skeleton
- [ ] Example Android Java Edition launcher skeleton

## Phase 5 — Tooling & Ecosystem
- [ ] Language Server Protocol (LSP)
- [ ] Formatter
- [ ] Build system plugin (Gradle)
- [ ] Documentation site
- [ ] Package registry / publishing

## Long-term
- Native backend (optional)
- Advanced performance features for tick loops
- Community-driven Minecraft libraries