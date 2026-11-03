# Packaging & Distribution (Draft)

## JVM / Minecraft
- L projects compile to normal `.jar` files
- Compatible with Fabric, Forge, NeoForge, Quilt loading mechanisms
- Can be published to Maven-compatible repositories

## Android
- Standard Android packaging (APK / AAB) for launcher apps
- Shared logic modules as Android libraries

## Web (`.le`)
- Bundled JS / WebAssembly-style outputs (exact backend TBD)
- Static assets + 3D models served as usual
- Deployable to normal static / modern web hosts

## Library Authors
- Publish multiplatform libraries with clear target support
- Document which modules work on jvm / android / web

## Versioning
- Semantic versioning for the language toolchain and standard library
- Clear compatibility guarantees as the language stabilizes