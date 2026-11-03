# L Standard Library Outline (Draft)

> Status: Design complete for Phase 0.  
> Modules cover core language needs plus Minecraft, launchers, UI, 3D, and web.

All general source files use **`.L`**. Website sources use **`.le`**.

## 1. Design Principles
- Kotlin standard library parity as baseline
- Small, focused modules
- Low-allocation options for hot paths
- Multiplatform from the start

## 2. Core Modules
- `l.core` — primitives, basics, null-safety helpers
- `l.collections` — lists, sets, maps, sequences
- `l.coroutines` — structured concurrency, Flow, channels
- `l.io` / `l.files` — paths and file operations
- `l.text` — strings and formatting
- `l.time` — duration, clocks
- `l.concurrent` — atomics / concurrent utilities

## 3. Minecraft Modules
- `l.minecraft` — Identifier, Registry, Edition, logging
- `l.minecraft.registry` — registry DSL
- `l.minecraft.entity` — components + entity DSL
- `l.minecraft.event` — on / onEach
- `l.minecraft.network` — packet DSL
- `l.minecraft.datagen` — models, recipes, tags, dual emission
- `l.minecraft.bedrock` — lower-level Bedrock support

## 4. Launcher Modules
- `l.launcher` — instances, versions, downloads, process launch
- `l.launcher.android` — Android paths, permissions, integration

## 5. UI Modules
- `l.ui` — Composable runtime, State, Modifier
- `l.ui.layout` — Column, Row, Box, Lazy grids/lists
- `l.ui.material` — design system components + theming
- `l.ui.animation` — transitions and animated values
- `l.ui.platform` — desktop & Android integration

## 6. 3D / Graphics Modules
- `l.math` — Vec2/3/4, Quat, Mat4
- `l.graphics` / `l.three` — Scene3D, Camera, Lights, Model, Mesh, Material, Animation
- Platform backends for desktop, Android, and web

## 7. Web Modules (`.le`)
- `l.web` — pages, routing, HTML/component model, browser interop
- `l.web.three` — web 3D backend (WebGL / WebGPU)
- Asset loading and bundling integration

## 8. Multiplatform Layout
```
common/   — shared logic, shared UI/3D abstractions
jvm/      — Minecraft Java, desktop launchers
android/  — Android launchers & UI
web/      — websites and web 3D
```

## 9. Versioning
Semantic versioning for toolchain and standard library; experimental modules clearly marked.