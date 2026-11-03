# L Compiler Prototype 0.0.14

## Minecraft target

**Fabric only** for now.

A new mod loader **Bear** is planned **after** the Fabric path is usable.

## Commands

```bash
./gradlew :compiler:run --args="compile examples/fabric/HelloFabric.L out Main"
./gradlew :compiler:run --args="fabric-json hellofabric HelloFabric out HelloFabric"
```

## Features

- Dual vocabulary (`fn`/`ret`/`let`/`obj` …)
- Multi-function + static fields on objects
- `fabric.mod.json` generator
- Fabric-oriented DSL analysis stub