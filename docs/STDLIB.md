# L Standard Library Outline (Draft)

> Status: Early design.  
> The standard library is organized into core modules plus domain-specific modules for Minecraft, launchers, and UI.

All source files use the **`.L`** extension.

## 1. Design Principles

- Kotlin standard library parity as the baseline
- Small, focused modules
- Low-allocation alternatives for hot paths (game loops, UI recomposition)
- Clear separation between core language support and Minecraft / UI domain libraries
- Multiplatform from the start (`common`, `jvm`, `android`, …)

## 2. Core Modules (Kotlin Parity + Extensions)

### `l.core`
- Basic types and top-level functions
- `Any`, `Unit`, `Nothing`, primitives, `String`, arrays
- Null-safety helpers
- `lazy`, `Delegates`, etc.

### `l.collections`
- `List`, `MutableList`, `Set`, `MutableSet`, `Map`, `MutableMap`
- `Sequence`, builders, higher-order functions
- Read-only vs mutable distinction

### `l.coroutines`
- Full structured concurrency
- `suspend` functions, `CoroutineScope`, `Job`, `Deferred`
- `Flow`, `Channel`, `Select`
- Dispatchers appropriate for JVM and Android

### `l.io` / `l.files`
- Path abstraction
- Basic file and directory operations
- Multiplatform expect/actual where needed

### `l.text`
- String builders, formatting
- (Later) richer text / adventure-style text for Minecraft

### `l.time`
- Duration, Instant, Clock abstractions

### `l.concurrent`
- Atomics, locks, concurrent collections (where appropriate)

## 3. Minecraft Modules

### `l.minecraft` (core)
- `Identifier`
- `Registry`, `RegistryKey`
- Edition enum and targeting helpers
- Common event interfaces
- Logging helpers (`logger()`)

### `l.minecraft.registry`
- Registry DSL implementation
- Deferred registration support
- Helpers for blocks, items, entities, etc.

### `l.minecraft.entity`
- High-level component types (`Health`, `Movement`, `Attack`, …)
- Entity builder / DSL
- Cross-edition component mapping

### `l.minecraft.event`
- `on` / `onEach` helpers
- Event bus abstractions
- Lifecycle-aware subscriptions

### `l.minecraft.network`
- Packet DSL
- Codec building blocks
- Client / server handler wiring

### `l.minecraft.datagen`
- Data generation entry points
- Model, recipe, tag, loot table helpers
- Dual emission (Java data packs + Bedrock definitions) when targeting both

### `l.minecraft.bedrock` (optional / advanced)
- Lower-level Bedrock component and Molang interop
- Used mainly by the cross-edition backend

## 4. Launcher Modules

### `l.launcher`
- Instance model
- Version / manifest handling
- Download and verification utilities
- Process launching abstractions
- Auth helpers (high-level)

### `l.launcher.android`
- Android-specific paths, permissions, activity integration
- JNI / native helpers when required

## 5. UI Modules (Built into L)

### `l.ui`
- `@Composable` runtime support
- `State`, `mutableStateOf`, `derivedStateOf`
- Basic composition and recomposition engine (or binding to an existing one)
- `Modifier` system

### `l.ui.layout`
- Column, Row, Box, Lazy lists/grids
- Alignment, arrangement, constraints

### `l.ui.material` (or similar design system)
- Buttons, Cards, TextFields, TopBars, FABs, …
- Theming and design tokens
- Typography, colors, shapes

### `l.ui.animation`
- Animated values
- Transitions
- Content size / visibility animations

### `l.ui.platform`
- Desktop windowing helpers
- Android Activity / ComposeView integration
- Input (pointer, keyboard, focus)

## 6. Multiplatform Layout

```
common/
  l.core
  l.collections
  l.coroutines
  l.minecraft          (common abstractions)
  l.ui                 (common UI)

jvm/
  l.minecraft.jvm      (Fabric/Forge/etc. implementations)
  l.launcher
  l.ui.desktop

android/
  l.launcher.android
  l.ui.android
```

## 7. Naming & Imports

```l
import l.minecraft.registry.*
import l.ui.*
import l.ui.material.*
```

Short, consistent package names. The language itself can later provide conveniences (auto-imports for the most common Minecraft and UI symbols inside `.L` files marked as mods or UI).

## 8. Versioning & Compatibility

- Semantic versioning for the standard library
- Clear separation between stable and experimental modules
- Binary compatibility goals aligned with Kotlin where practical

## 9. Open Questions

- How much of the Kotlin stdlib to re-export vs. re-implement?
- Exact relationship with Jetpack Compose / Compose Multiplatform (bind vs. reimplement)
- Size of the default “Minecraft starter” dependency set
- Whether some DSLs should live in the compiler (intrinsic) or pure library code

---

Next concrete steps:
- Define the exact public API surface of `l.minecraft` and `l.ui`
- Prototype the registry and composable runtimes