# L Language Roadmap

## Phase 0 — Vision & Design ✅ COMPLETE
- [x] Full language design documentation
- [x] Examples (`.L` and `.le`)

## Phase 1 — Specification Hardening
- [ ] Freeze core syntax decisions
- [ ] Expand formal semantics where needed
- [ ] Finalize stdlib module boundaries

## Phase 2 — Compiler Prototype 🚧 IN PROGRESS
- [x] Project structure (Gradle + Kotlin)
- [x] Lexer (working)
- [x] Minimal parser + AST
- [x] CLI: `lex`, `parse`, `version`
- [x] Basic unit tests
- [ ] Expand parser toward full grammar
- [ ] Name resolution & type checking (subset)
- [ ] JVM bytecode backend (basic)
- [ ] Gradle wrapper + polished CLI distribution

## Phase 3 — Minecraft Modules
- [ ] `l.minecraft` prototypes
- [ ] Example Fabric / NeoForge mod

## Phase 4 — Cross-Edition
- [ ] Bedrock backend prototype
- [ ] First bidirectional port examples

## Phase 5 — Launcher + UI + 3D
- [ ] Launcher + UI prototype
- [ ] Desktop 3D prototype

## Phase 6 — Web & 3D Web
- [ ] `.le` web backend
- [ ] Web 3D backend

## Phase 7 — Tooling & Ecosystem
- [ ] LSP, formatter, publishing