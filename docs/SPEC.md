# L Language Specification (Draft)

> Status: Early draft. This document will evolve significantly.

## 1. Introduction

L is a statically typed, multiplatform programming language with full Kotlin feature parity, extended with domain-specific features for Minecraft modding and launcher development.

Primary compilation target: **JVM**  
Secondary target: **Android** (for Java Edition launchers)

## 2. Design Goals (Summary)

- Complete Kotlin syntax and semantics as baseline
- First-class support for Minecraft registries, events, networking, and data generation
- Excellent interop with existing Java and Kotlin code
- Strong performance characteristics for game loops and launchers
- Multiplatform project model from the start

## 3. Lexical Structure

L uses the same lexical structure as Kotlin:

- Unicode identifiers
- Soft keywords
- String templates
- Raw strings (`"""`)
- Comments: `//` and `/* */`

## 4. Types

### 4.1 Basic Types
Same as Kotlin: `Int`, `Long`, `Float`, `Double`, `Boolean`, `Char`, `String`, `Unit`, `Nothing`, etc.

### 4.2 Null Safety
Identical to Kotlin:
- Nullable types with `?`
- Safe calls `?.`
- Elvis operator `?:`
- Not-null assertion `!!`
- Smart casts

### 4.3 Special Minecraft-oriented Types (planned)
- `Identifier` / `ResourceLocation` (compile-time checked where possible)
- `RegistryKey<T>`
- Strongly-typed packet types
- Block / Item / Entity references with registry awareness

## 5. Declarations

L supports all Kotlin declaration forms:

- `fun`, `val`, `var`
- `class`, `interface`, `object`, `enum class`, `annotation class`
- `data class`, `sealed class` / `sealed interface`
- `value class` / `inline class`
- Type aliases
- Extensions

## 6. Control Flow

Identical to Kotlin:
- `if`, `when`, `for`, `while`, `do-while`
- `return`, `break`, `continue`, `throw`
- Labels and qualified returns

## 7. Functions & Lambdas

Full Kotlin support including:
- Default parameters
- Named arguments
- Trailing lambdas
- `inline`, `crossinline`, `noinline`
- `suspend` functions
- Context receivers / parameters

## 8. Coroutines

Full structured concurrency model from Kotlin:
- `suspend` functions
- `coroutineScope`, `supervisorScope`
- Flows and Channels
- Structured concurrency guarantees

## 9. Multiplatform

L adopts Kotlin Multiplatform project structure concepts:
- `common`, `jvm`, `android` (and future) source sets
- `expect` / `actual` declarations

## 10. Minecraft Domain Features (Planned)

These are **extensions** on top of the Kotlin baseline:

### 10.1 Registry DSL
Type-safe registration of blocks, items, entities, etc.

### 10.2 Event Handling
Idiomatic event subscription with automatic unregistering and coroutine support.

### 10.3 Networking
Declarative packet definition with codecs and handlers.

### 10.4 Data Generation
First-class support for generating assets, tags, recipes, etc.

### 10.5 Identifier Safety
Compile-time or strongly-typed resource locations to reduce runtime errors.

## 11. Interoperability

- L can call any Java or Kotlin code
- Java/Kotlin can call L code (with standard JVM naming)
- Annotations and binary compatibility follow Kotlin conventions where possible

## 12. Versioning

Language versions will follow a clear compatibility policy (to be defined).

---

This is a living document. Contributions and feedback are welcome.