# L Type System Outline (Draft)

> Status: Early design. Builds on the Kotlin type system with Minecraft- and UI-oriented extensions.

## 1. Design Goals

- Full Kotlin type system parity as the baseline
- Strong null safety
- Excellent type inference
- First-class support for Minecraft domain types (identifiers, registries, components…)
- Support for declarative UI types (composables, modifiers, state…)
- Clear multiplatform and cross-edition typing rules

## 2. Core Types (Kotlin Parity)

### 2.1 Primitive & Basic Types
```
Int, Long, Short, Byte, Float, Double, Boolean, Char, String
Unit, Nothing, Any
```

### 2.2 Nullability
- `T` — non-nullable
- `T?` — nullable
- Safe call `?.`, Elvis `?:`, not-null assertion `!!`
- Smart casts after null checks

### 2.3 Function Types
```
(A, B) -> R
suspend (A) -> R
```

### 2.4 Generics
- Type parameters with variance (`in`, `out`)
- Reified type parameters
- Star projections
- Type constraints / upper bounds

### 2.5 Special Class Kinds
- `data class`
- `sealed class` / `sealed interface`
- `value class` / `inline class`
- `enum class`
- `annotation class`
- `object` / `companion object`

## 3. Minecraft Domain Types

These types are part of the standard Minecraft module and participate fully in the type system.

### 3.1 Identifiers
```l
value class Identifier(
    val namespace: String,
    val path: String
)

// Convenience
fun id(namespace: String, path: String): Identifier
fun String.toId(): Identifier          // "minecraft:stone".toId()
```

Compile-time / strongly-typed helpers where possible:
```l
val STONE = id("minecraft", "stone")   // known at compile time when constant
```

### 3.2 Registry Keys & Entries
```l
class RegistryKey<T>(val registry: Identifier, val value: Identifier)

interface Registry<T> {
    operator fun get(id: Identifier): T?
    fun getOrThrow(id: Identifier): T
    // ...
}
```

### 3.3 Component System (Cross-Edition)
```l
interface Component

// High-level components that can be lowered to Java or Bedrock
class HealthComponent(val max: Float, var current: Float) : Component
class MovementComponent(val speed: Float) : Component
// ...
```

### 3.4 Edition Targets
```l
enum class Edition {
    Java,
    Bedrock
}

// Used with multiplatform-style expect/actual and @Target
```

### 3.5 Events & Packets
```l
interface Event

interface Packet {
    // codec information attached via DSL / annotations
}
```

## 4. UI Types (Built into L)

UI is part of the language. The type system supports declarative UI constructs.

### 4.1 Composable Functions
```l
@Composable
fun HomeScreen(state: LauncherState) { ... }
```

The `@Composable` annotation (or equivalent language marker) participates in type checking:
- Only composable functions may call other composable functions
- State reads are tracked for recomposition

### 4.2 State & Reactivity
```l
interface State<T> {
    var value: T
}

fun <T> mutableStateOf(initial: T): State<T>
fun <T> derivedStateOf(calculation: () -> T): State<T>
```

### 4.3 Modifiers & Layout
```l
interface Modifier {
    fun then(other: Modifier): Modifier
}

// Common modifiers are extension functions
fun Modifier.padding(all: Dp): Modifier
fun Modifier.fillMaxSize(): Modifier
// ...
```

### 4.4 Dp / Sp and Density-independent Units
```l
value class Dp(val value: Float)
value class Sp(val value: Float)
```

## 5. Multiplatform & Cross-Edition Typing

### 5.1 expect / actual
```l
// common
expect fun getGameDir(): Path

// jvm / android actuals
actual fun getGameDir(): Path = ...
```

### 5.2 Edition-specific actuals
```l
@Target(Edition.Java, Edition.Bedrock)
entity("demo:robot") { ... }

actual(Edition.Java) { ... }
actual(Edition.Bedrock) { ... }
```

The type checker verifies that required actuals exist for every targeted edition.

## 6. Type Inference

L aims for the same level of local type inference as Kotlin:
- Variable types from initializers
- Lambda parameter types from context
- Generic type argument inference
- Return type inference for single-expression functions

Additional inference goals:
- Identifier / registry context inference inside DSLs
- Composable context propagation

## 7. Subtyping & Variance

Follows Kotlin rules:
- Declaration-site variance (`in`, `out`)
- Use-site variance / projections
- Nullability as part of the type lattice
- `Nothing` is bottom, `Any?` is top

## 8. Error Types & Diagnostics

The type system should produce high-quality diagnostics:
- Null-safety violations
- Missing actual implementations for targeted editions
- Invalid registry / identifier usage
- Calling non-composable code from a composable context
- Capability mismatches when targeting both Java and Bedrock

## 9. Open Questions

- How much compile-time validation of resource locations is feasible?
- Exact representation of Bedrock component types vs Java classes
- Degree of type-level tracking for UI recomposition
- Interop rules when calling Java/Kotlin libraries that use platform types

---

Next steps:
- Flesh out the full lattice and subtyping rules
- Define the precise shape of Minecraft component types
- Specify UI composable type rules in more detail