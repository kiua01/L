# L Major Feature Extensions

## 1. Cross-Edition Portability (Bedrock ↔ Java)

One of the most ambitious goals of L is to make **bidirectional porting** between Minecraft ecosystems dramatically easier:

- Port any **Bedrock Add-on** → **Java Edition Mod**
- Port any **Java Edition Mod** → **Bedrock Add-on**

L provides a unified high-level model that can target both ecosystems, with clear diagnostics when features differ.

---

## 2. Modern UI (Built into L)

L includes first-class support for building **very modern**, beautiful, fluid interfaces for launchers and tools — all in the same language.

- Declarative & reactive
- Excellent desktop + Android support
- Deep integration with the rest of L

---

## 3. First-Class 3D

L is designed to be **very good at 3D**.

This covers:

- 3D for games and tools
- 3D scenes inside launchers
- 3D on the web (3D websites)
- Real-time rendering, models, cameras, lights, materials, animations

### Goals

- High-level, productive 3D APIs
- Good performance for real-time use
- Same language for logic + 3D + UI
- Works on desktop, Android, and the web

### Example Vision

```l
@Composable
fun CharacterPreview(model: Model3D) {
    Scene3D {
        Camera(position = Vec3(0f, 1.5f, 4f), lookAt = Vec3.ZERO)
        AmbientLight(intensity = 0.4f)
        DirectionalLight(direction = Vec3(-1f, -1f, -0.5f))

        Model(
            source = model,
            position = Vec3.ZERO,
            onClick = { /* ... */ }
        )
    }
}
```

---

## 4. Websites & 3D Websites

L is also designed to be **very good for making websites**, including **3D websites**.

### File Extension for Web

**Website-related source files use the `.le` extension.**

| Kind of code              | Extension |
|---------------------------|-----------|
| Mods, launchers, general  | `.L`      |
| Websites & web 3D         | `.le`     |

Examples:
- `HomePage.le`
- `ProductViewer3D.le`
- `Main.L` (launcher or mod)
- `RobotEntity.L`

### Web Capabilities

- Modern, reactive web UIs
- Component-based architecture (same spirit as the rest of L)
- First-class **3D on the web** (WebGL / WebGPU style backends)
- Shared logic between desktop/Android launchers and web experiences where it makes sense

### Example Vision (Website)

```le
@page
fun HomePage() {
    Html {
        Head { Title("My 3D Site") }
        Body {
            HeroSection()
            ProductViewer3D(model = "/models/robot.glb")
            Footer()
        }
    }
}

@Composable
fun ProductViewer3D(model: String) {
    Scene3D(modifier = Modifier.height(480.px)) {
        Camera(...)
        Model(source = model)
    }
}
```

---

## 5. One Language Family

| Area                        | How L helps                                      | Extension |
|-----------------------------|--------------------------------------------------|-----------|
| Java Edition mods           | DSLs, interop, performance                       | `.L`      |
| Bedrock ↔ Java porting      | Unified model + backends                         | `.L`      |
| Launchers (PC + Android)    | Shared logic + modern UI                         | `.L`      |
| Modern desktop/mobile UI    | Declarative UI built into the language           | `.L`      |
| 3D (games, tools, viewers)  | High-level 3D APIs                               | `.L` / `.le` |
| Websites                    | Modern reactive web                              | `.le`     |
| 3D websites                 | 3D scenes on the web                             | `.le`     |

> **L — One language for mods, launchers, UI, 3D, websites, and 3D websites.**

---

## Summary

| Feature                        | Status |
|--------------------------------|--------|
| Cross-Edition Portability      | Design |
| Modern UI (built into L)       | Design |
| First-class 3D                 | Design |
| Websites + 3D websites         | Design |
| `.L` for general code          | Decided |
| `.le` for website code         | Decided |