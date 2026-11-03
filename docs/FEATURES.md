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
            player.sendMessage("Hello from L!")
            spawnParticle("demo:spark", position)
        }
    }
}
```

The same source could produce a Java mod *and* a Bedrock add-on.

---

## 2. Modern UI Capabilities (Built into L)

L is **one language for everything** — including modern user interfaces for launchers and tools.

There is **no separate UI language**. UI features are a first-class part of L itself.

### Goals

- Create **very modern**, beautiful, fluid interfaces
- First-class support for desktop *and* mobile (especially Android launchers)
- Excellent performance and smooth animations
- Deep integration with the rest of L (shared types, easy calling of backend logic)
- Compatible with modern UI paradigms (declarative, reactive, component-based)

### How it works

L includes a rich UI module and declarative syntax that compiles to modern UI targets:

```
L UI code  ──compiles to──►  Modern UI backends
                              ├── Compose Multiplatform / Skia
                              ├── Native Android (Jetpack Compose)
                              ├── Desktop (Windows / macOS / Linux)
                              └── (Future) Web / other
```

### Example Vision (UI in L)

```l
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

### Key UI Features Planned

- Declarative component model
- Fine-grained reactivity
- Built-in animation and transition system
- Design tokens / theming engine
- Excellent Android + Desktop support out of the box
- Hot reload for rapid UI development
- Seamless calls into the rest of L code (game launching, instance management, downloads, etc.)

---

## 3. File Extension

**All L source files use the `.L` extension** (uppercase).

Examples:
- `Main.L`          — mod entry point
- `RobotEntity.L`   — cross-edition entity
- `HomeScreen.L`    — modern launcher UI
- `LauncherCore.L`  — shared launcher logic

One language. One extension. Everything.

---

## Summary

| Feature                        | Description                                      | Status     |
|--------------------------------|--------------------------------------------------|------------|
| Cross-Edition Portability      | Bedrock Add-on ↔ Java Mod                        | Design     |
| Modern UI (built into L)       | Beautiful launcher & tool interfaces             | Design     |
| Single language + `.L` files   | One language for mods, launchers, UI, everything | Design     |

> **L — One language for the entire Minecraft creator and launcher ecosystem.**