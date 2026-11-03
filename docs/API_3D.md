# 3D API Surface (Draft)

## Goals
- Productive high-level API
- Real-time friendly
- Shared concepts across desktop, Android, and web

## Math
```l
value class Vec2(val x: Float, val y: Float)
value class Vec3(val x: Float, val y: Float, val z: Float)
value class Vec4(...)
value class Quat(...)
value class Mat4(...)
```

## Scene
```l
Scene3D {
    Camera(...)
    AmbientLight(...)
    DirectionalLight(...)
    Model(...)
    Mesh(...)
}
```

## Core Types
- `Camera` — perspective / orthographic
- `Light` — ambient, directional, point, spot
- `Model` — loaded glTF/GLB (primary), other formats later
- `Mesh` — low-level geometry
- `Material` — PBR-oriented defaults
- `Texture`
- `Animation` / `AnimationPlayer`
- `Transform` — position, rotation, scale

## Interaction
- Pointer pick / drag
- Hover
- Simple physics hooks (optional, later)

## Platforms
- Desktop: modern GPU APIs
- Android: mobile-friendly backend
- Web: WebGL / WebGPU via `.le` projects

## Performance Notes
- Prefer instancing and batched draws in the runtime
- Keep per-frame allocations low in user code
- Allow escape hatches for advanced users later