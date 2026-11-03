# L → Fabric Target (Current)

> **Now:** Fabric only.  
> **Later:** a new mod loader codenamed **Bear** (after the Fabric path is solid).

## Goals

L should make Fabric mods feel native:

- `fabric.mod.json` generation
- Entrypoints (`ModInitializer`, client, server)
- Registry DSL → Fabric `Registry.register`
- Item / block / entity helpers
- Data generation hooks (optional)

## Project layout (vision)

```
my-mod/
  l.toml
  src/main/
    MyMod.L
    fabric.mod.json   # or generated
  build.gradle.kts    # Fabric Loom
```

## Minimal mod shape

```l
pkg com.example.mymod

imp net.fabricmc.api.ModInitializer

obj MyMod : ModInitializer {
  ovrd fn onInitialize() {
    println("Hello Fabric from L!")
  }
}
```

Abbreviated style:

```l
pkg com.example.mymod

obj MyMod {
  fn onInitialize() {
    println("Hello Fabric from L!")
  }
}
```

## Registry sketch

```l
obj ModBlocks {
  let COOL_STONE = reg.block("mymod:cool_stone") {
    // settings…
  }
}
```

Full emission to real Fabric bytecode is **in progress**. The compiler currently:

1. Parses L (including dual vocabulary)
2. Analyzes Minecraft-shaped declarations
3. Emits JVM classfiles for logic
4. Records Fabric metadata hooks for upcoming Loom integration

## Non-goals (for now)

- Forge / NeoForge / Quilt backends
- Bear loader (planned after Fabric track)

## Roadmap slice

1. Stable JVM backend for mod logic
2. `fabric.mod.json` generator
3. Loom Gradle plugin skeleton
4. Registry + item/block helpers
5. Then design **Bear** as a first-class L-native loader