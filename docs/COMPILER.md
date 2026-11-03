# L Compiler Architecture (Draft)

> Status: Early design notes.

## Overview

The L compiler will be designed as a multi-stage pipeline with clear separation of concerns, inspired by modern compilers (Kotlin, Rust, Swift) while remaining pragmatic for a JVM-first language.

```
Source (.l)
    ↓
Lexer
    ↓
Parser  →  CST / AST
    ↓
Name Resolution & Import Resolution
    ↓
Type Checking & Inference
    ↓
L-specific Lowering (Minecraft DSLs → normal constructs)
    ↓
IR (Intermediate Representation)
    ↓
Backend(s)
    ├── JVM Bytecode
    ├── Android (DEX / related)
    └── (Future) Native
```

## 1. Frontend

### 1.1 Lexer
- Hand-written or generated
- Must support Kotlin-compatible tokens + L soft keywords
- Good error recovery and precise source locations

### 1.2 Parser
- Prefer a recursive-descent or PEG-style parser for better error messages
- Produce a Concrete Syntax Tree (CST) first, then lower to AST
- Preserve all source information for IDE / LSP use

### 1.3 AST
- Immutable, well-typed AST nodes
- Rich source mapping (for diagnostics and IDE features)

## 2. Semantic Analysis

### 2.1 Name Resolution
- Hierarchical scopes (file, package, class, function, block)
- Support for Kotlin-style imports, star imports, aliases
- Package-level and file-level declarations

### 2.2 Type System
- Full Kotlin type system as baseline:
  - Nullability
  - Generics (including variance, reified)
  - Function types
  - Intersection / union approximations where needed
  - Type inference (local + limited global)
- Additional L types:
  - `Identifier` / resource location types
  - Registry-aware types
  - Packet types

### 2.3 L DSL Lowering
Special syntactic forms are lowered into regular function calls + builders early:

| Surface Syntax              | Lowers to                          |
|----------------------------|------------------------------------|
| `registry<Block> { ... }`  | `RegistryBuilder<Block>().apply{}` |
| `on<Event> { ... }`        | `EventBus.subscribe<Event> {}`     |
| `packet<T> { ... }`        | `PacketDefinition.create<T> {}`    |

This keeps the core type checker simpler while still providing nice syntax.

## 3. Intermediate Representation (IR)

Goals for IR:
- Language-agnostic enough for multiple backends
- Good support for coroutines (CPS or state-machine lowering)
- Ability to represent value classes / inline classes efficiently
- Easy to perform optimizations (especially allocation elimination)

Possible IR choices (to be decided):
- Custom IR
- Kotlin IR (if integrating closely with Kotlin compiler infrastructure)
- LLVM-like SSA form for future native backend

## 4. Backends

### 4.1 JVM Backend (Primary)
- Emit standard JVM bytecode
- Full interop with Java and Kotlin
- Support for:
  - Coroutines (state machines)
  - Inline classes
  - Default parameters
  - Suspend functions

### 4.2 Android Backend
- Builds on JVM backend
- Additional considerations for:
  - DEX compatibility
  - Android-specific APIs
  - Smaller runtime footprint for launchers

### 4.3 Future Native Backend
- Optional, for performance-critical launcher components
- Would require a different runtime model

## 5. Diagnostics

High-quality error messages are a priority:
- Precise source locations
- Suggestions (“Did you mean …?”)
- Notes explaining nullability or type mismatches
- Minecraft-specific diagnostics (e.g. invalid registry names, missing codecs)

## 6. Tooling Integration Points

The compiler should expose:
- Full AST + semantic model for LSP
- Incremental compilation support
- Compiler plugins API (later)
- Stable binary metadata format for libraries

## 7. Implementation Language

Options under consideration:
1. **Kotlin** — natural choice, good interop, can potentially reuse parts of Kotlin compiler
2. **Rust** — excellent for performance and correctness of the compiler itself
3. **Hybrid** — frontend in Kotlin, performance-critical parts in Rust

Decision will be made based on contributor availability and long-term goals.

## 8. Build & Packaging

- Gradle plugin for L projects
- Ability to publish L libraries as normal JVM artifacts
- Source sets for multiplatform (`common`, `jvm`, `android`)

## 9. Open Questions

- How closely should L integrate with the Kotlin compiler frontend?
- Should L support compiling mixed Kotlin + L projects seamlessly?
- Degree of binary compatibility with Kotlin metadata?
- Incremental compilation strategy?
- How aggressive should Minecraft DSL lowering be?

---

## Next Concrete Steps

1. Decide implementation language for the prototype
2. Build a minimal lexer + parser for a subset of the language
3. Implement a tiny type checker for a core subset
4. Emit simple JVM bytecode for “Hello World” + basic functions
5. Add first Minecraft DSL lowering (registry)