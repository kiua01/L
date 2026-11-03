# L — 3D & Web Design (Draft)

> Status: Early design.  
> L aims to be **very good at 3D** and **very good for websites**, including **3D websites**.

## 1. File Extensions

| Purpose                         | Extension |
|---------------------------------|-----------|
| Mods, launchers, general logic, desktop/mobile UI | `.L` |
| Websites and web 3D             | `.le` |

Examples:
- `Main.L` — mod or launcher entry
- `HomeScreen.L` — desktop/Android UI
- `HomePage.le` — website page
- `ProductViewer3D.le` — 3D viewer on the web

## 2. 3D Goals

L should make 3D work productive and high-quality for:

- Game tools and viewers
- Character / item previews in launchers
- Interactive 3D on websites
- Real-time scenes with models, lights, cameras, materials, and animation

### Core 3D Concepts (Vision)

```l
Scene3D { ... }
Camera(...)
AmbientLight(...)
DirectionalLight(...)
PointLight(...)
Model(source = ..., position = ..., scale = ...)
Mesh(...)
Material(...)
AnimationPlayer(...)
```

### Targets

- Desktop (OpenGL / Vulkan / Metal style backends)
- Android
- Web (WebGL / WebGPU)

Same high-level API, different backends.

## 3. Websites

L is designed for modern websites:

- Component-based, reactive UI (same philosophy as the rest of L)
- Good integration with 3D
- Clean routing and page model
- Ability to share logic with non-web L code where it makes sense

### Basic Page Vision

```le
@page
fun HomePage() {
    Html {
        Head {
            Title("L 3D Site")
            Meta(name = "viewport", content = "width=device-width, initial-scale=1")
        }
        Body {
            NavBar()
            Hero()
            Features()
            ProductViewer3D(model = "/models/robot.glb")
            Footer()
        }
    }
}
```

### 3D on the Web

```le
@Composable
fun ProductViewer3D(model: String) {
    Scene3D(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.px)
    ) {
        Camera(
            position = Vec3(0f, 1.2f, 3.5f),
            lookAt = Vec3.ZERO
        )
        AmbientLight(0.35f)
        DirectionalLight(Vec3(-1f, -1f, -0.8f))

        Model(
            source = model,
            onPointerDrag = { delta ->
                // rotate model
            }
        )
    }
}
```

## 4. Relationship to the Rest of L

- **Logic** (downloads, instances, registries, networking…) stays in normal L modules
- **Desktop / Android UI** uses `.L` + the UI module
- **Web pages & web 3D** use `.le`
- **3D engine / APIs** are shared concepts that can target multiple platforms

This keeps one language while giving web projects a clear, dedicated file extension.

## 5. Standard Library Directions

### `l.graphics` / `l.three`
- Math (Vec2, Vec3, Vec4, Mat4, Quat)
- Scene graph basics
- Cameras, lights, materials
- Model loading (glTF / GLB first)
- Animation

### `l.web`
- Page / routing model
- HTML-like builders or component model
- Browser interop
- Asset loading

### `l.web.three`
- Web-specific 3D backend (WebGL / WebGPU)
- Canvas integration with the web UI

## 6. Tooling Notes

- `.L` and `.le` files both handled by the L toolchain
- LSP, formatter, and build tools understand both extensions
- Web projects can be bundled for static hosting or modern web deployment

## 7. Open Questions

- Exact 3D API surface (how high-level vs. low-level)
- Choice of primary web 3D backend (WebGL vs WebGPU priority)
- How much HTML is exposed vs. pure component model
- SSR / static generation support
- Sharing UI components between desktop and web

---

L’s ambition:

> One language that can power Minecraft mods, launchers, modern UIs, real-time 3D, websites, and 3D websites.