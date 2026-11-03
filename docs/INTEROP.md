# Interoperability with Java & Kotlin (Draft)

## Goals
- Call any Java or Kotlin library from L
- Allow Java/Kotlin to call L where practical
- Publish L libraries as normal JVM artifacts
- Minimize friction for existing Minecraft mod ecosystems

## L → Java / Kotlin
- All public L declarations compile to standard JVM bytecode
- Nullability is enforced on the L side; platform types from Java are handled carefully
- Kotlin `suspend` and L `suspend` interoperate via the same coroutine conventions where possible

## Java / Kotlin → L
- L classes and functions are visible as normal JVM classes/methods
- Naming follows Kotlin-like conventions (or configurable)
- Annotations and binary metadata aim for tool compatibility

## Minecraft Ecosystem
- Fabric / Forge / NeoForge / Quilt mods written in L should load like any other mod
- Existing Java/Kotlin mods remain callable
- Mixins and access wideners remain available on the Java target

## Android
- L code targeting Android uses standard Android / JVM interop rules
- JNI is available when needed for launcher or native components

## Web (`.le`)
- Interop with JavaScript/TypeScript is via explicit bindings / FFI-style APIs
- Prefer keeping core logic in L and exposing a clean surface to the browser

## Recommendations
- Prefer pure L for new Minecraft domain code
- Use Java/Kotlin libraries freely for infrastructure
- Isolate edition-specific or platform-specific interop behind `expect`/`actual`