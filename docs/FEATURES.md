# L Major Feature Extensions

## 1. Cross-Edition Portability (Bedrock ↔ Java)

One of the most ambitious goals of L is to make **bidirectional porting** between Minecraft ecosystems dramatically easier:

- Port any **Bedrock Add-on** → **Java Edition Mod**
- Port any **Java Edition Mod** → **Bedrock Add-on**

### Why this matters

Today these two ecosystems are almost completely separate:
- Different languages (Java/Kotlin vs. JavaScript/JSON + Molang)
- Different APIs, events, component systems, and packaging
- Different performance and capability models

L aims to provide a **common high-level model** that can target both.

### Design Approach

```
                ┌─────────────────────┐
                │   L Source Code     │
                │  (common model)     │
                └─────────┬───────────┘
                          │
           ┌──────────────┼──────────────┐
           ▼                             ▼
┌─────────────────────┐       ┌─────────────────────┐
│  Java Edition        │       │  Bedrock Edition     │
│  Backend             │       │  Backend             │
│  (Fabric/Forge/etc.) │       │  (Add-on / Behavior  │
│                      │       │   + Resource Packs)  │
└─────────────────────┘       └─────────────────────┘
```

### Key Ideas

1. **Unified Entity / Block / Item Model**  
   L provides high-level abstractions for components, events, and behaviors that can be lowered to either:
   - Java classes + mixins / events, or
   - Bedrock component JSON + Molang + scripts

2. **Behavior Description Language**  
   Developers describe *what* something does, not *how* it is implemented on a specific edition.

3. **Capability Mapping Layer**  
   Not every feature exists on both sides. L will:
   - Provide clear diagnostics when a feature cannot be fully ported
   - Offer polyfills / approximations where possible
   - Allow edition-specific `actual` implementations (similar to multiplatform)

4. **Asset & Data Pipeline**  
   Shared data generation that can emit:
   - Java data packs + assets
   - Bedrock behavior + resource packs

5. **Scripting Bridge**  
   For complex logic, L can generate:
   - JVM bytecode for Java
   - Optimized JavaScript / Molang for Bedrock

### Example Vision

```l
// This entity definition can target both editions
entity("demo:robot") {
    components {
        health(20)
        movement(0.25)
        attack(damage = 3)
    }

    events {
        onInteract { player ->
            // High-level logic — compiler decides how to implement
            player.sendMessage("Hello from L!")
            spawnParticle("demo:spark", position)
        }
    }
}
```

The same source could produce a Java mod *and* a Bedrock add-on.

---

## 2. Derived UI Language — “Lume”

L will have a **sister language** (working name: **Lume**) specifically designed for building modern launcher and tool UIs.

### Goals of Lume

- Create **very modern**, beautiful, fluid interfaces
- First-class support for desktop *and* mobile (especially Android launchers)
- Excellent performance and smooth animations
- Deep integration with L (shared types, easy calling of L backend logic)
- Compatible with modern UI paradigms (declarative, reactive, component-based)

### Relationship to L

```
Lume  ──uses──►  L libraries & runtime
  │
  └── compiles to modern UI targets
        ├── Compose Multiplatform / Skia
        ├── Native Android (Jetpack Compose)
        ├── Desktop (Windows / macOS / Linux)
        └── (Future) Web / other
```

Lume is **derived from L**:
- Shares the same core type system and many language features
- Adds a rich declarative UI syntax and reactive primitives
- Optimized for UI state, layouts, animations, and theming

### Example Vision (Lume)

```lume
@Composable
fun LauncherHome(state: LauncherState) {
    Screen {
        TopBar(title = "L Launcher")

        InstanceGrid(
            instances = state.instances,
            onSelect = { instance -> state.launch(instance) }
        )

        FloatingActionButton(
            onClick = { state.createNewInstance() }
        ) {
            Icon(Icons.Add)
        }
    }
}
```

### Key Features Planned for Lume

- Declarative component model
- Fine-grained reactivity (no unnecessary recomposition)
- Built-in animation and transition system
- Design tokens / theming engine
- Excellent Android + Desktop support out of the box
- Hot reload for rapid UI development
- Seamless calls into L code (for game launching, instance management, downloads, etc.)

### Why a separate language?

UI development has different needs (layout, reactivity, animation, accessibility) from game logic and modding. By deriving a specialized language from L we keep the core powerful while giving UI developers the best possible experience.

---

## Summary

| Feature                        | Description                                      | Status     |
|--------------------------------|--------------------------------------------------|------------|
| Cross-Edition Portability      | Bedrock Add-on ↔ Java Mod                        | Design     |
| Lume (UI Language)             | Modern launcher & tool interfaces                | Design     |
| Shared L core                  | Common types, multiplatform, interop             | Design     |

These two features significantly expand L’s mission:

> **One language family for the entire Minecraft creator and launcher ecosystem.**