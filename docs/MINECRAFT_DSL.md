# Minecraft DSL Design (Draft)

> Status: Early design. These DSLs are the primary way developers interact with Minecraft concepts in L.

All examples use the official **`.L`** extension.

## 1. Design Principles

- Feel natural to Kotlin developers
- Prefer declarative style for registration and configuration
- Keep hot paths (tick, render, networking) low-allocation
- Support both Java Edition and Bedrock targets where possible
- Lower to idiomatic code for each platform

## 2. Mod Entry Point

```l
@Mod("example")
object ExampleMod {

    val LOGGER = logger()

    init {
        // called on mod initialization
    }
}
```

For multi-edition:
```l
@Mod("example")
@Target(Edition.Java, Edition.Bedrock)
object ExampleMod { ... }
```

## 3. Registry DSL

### 3.1 Basic Usage

```l
val BLOCKS = registry<Block> {
    "cool_stone" {
        settings {
            mapColor = MapColor.STONE
            hardness = 1.5f
            resistance = 6.0f
            requiresTool = true
        }
    }

    "glowing_ore" {
        settings {
            luminance = 12
            hardness = 3.0f
        }
    }
}

val ITEMS = registry<Item> {
    "cool_stone" {
        fromBlock(BLOCKS["cool_stone"])
    }

    "magic_sword" {
        settings {
            maxCount = 1
            maxDamage = 500
        }
    }
}
```

### 3.2 Design Notes

- The string key is relative to the mod id unless a full Identifier is given
- Nested `settings { }` is a type-safe builder
- `fromBlock` creates the corresponding BlockItem automatically
- The registry object itself is usable as a map-like structure: `BLOCKS["cool_stone"]`

## 4. Entity / Component DSL (Cross-Edition)

```l
@Target(Edition.Java, Edition.Bedrock)
entity("demo:robot") {

    components {
        health(max = 30)
        movement(speed = 0.28)
        attack(damage = 4.0f, range = 2.5f)
        inventory(size = 9)

        model("demo:robot")
        texture("demo:robot")
    }

    events {
        onSpawn {
            playSound("demo:robot_activate")
        }

        onInteract { player ->
            player.sendMessage("Hello from L!")
        }

        onHurt { attacker, amount ->
            if (health.current < 10) {
                playSound("demo:robot_alarm")
                spawnParticle("demo:smoke", at = position)
            }
        }
    }

    // Optional edition-specific refinements
    actual(Edition.Java) {
        // Java-only details
    }

    actual(Edition.Bedrock) {
        // Bedrock-only details / Molang tweaks
    }
}
```

### Goals
- One definition → two targets when possible
- Clear diagnostics when a component or event cannot be represented on a target edition

## 5. Event Handling

```l
// Fire-and-forget subscription
on<ServerLifecycleEvents.ServerStarted> { server ->
    LOGGER.info("Server started")
}

// Per-tick (must stay allocation-free)
onEach<ServerTickEvent> { event ->
    // hot path
}
```

- `on` — subscribe once
- `onEach` — intended for high-frequency events
- Automatic lifecycle management (unregister when the mod is unloaded)

## 6. Networking / Packet DSL

```l
packet<HelloPacket>(id = "example:hello") {
    // Define the data
    codec {
        string("message")
        int("count")
    }

    // Server-side handler
    serverHandler { packet, ctx ->
        ctx.player.sendMessage(Text.literal(packet.message))
    }

    // Optional client-side handler
    clientHandler { packet, ctx ->
        // ...
    }
}
```

The compiler + library generate:
- Packet class
- Codec
- Registration
- Handler wiring

## 7. Data Generation

```l
datagen {
    models {
        block(BLOCKS["cool_stone"]) {
            // model configuration
        }
    }

    recipes {
        shaped(ITEMS["magic_sword"]) {
            pattern(
                " S ",
                " S ",
                " T "
            )
            ingredient('S', Items.DIAMOND)
            ingredient('T', Items.STICK)
        }
    }

    tags {
        block("minecraft:mineable/pickaxe") {
            add(BLOCKS["cool_stone"])
        }
    }

    // Can also emit Bedrock-compatible definitions when targeting both editions
}
```

## 8. Identifier Helpers

```l
val id1 = id("example", "cool_stone")
val id2 = "example:cool_stone".toId()
val id3 = id("cool_stone")          // uses current mod namespace
```

Inside a mod context the namespace can be omitted for convenience.

## 9. Lowering Strategy

| Surface DSL              | Lowers toward                                      |
|--------------------------|----------------------------------------------------|
| `registry { }`           | Registry builder + deferred registration           |
| `entity { }`             | Java entity class / Bedrock entity JSON + scripts  |
| `on` / `onEach`          | Event bus subscription                             |
| `packet { }`             | Packet class + codec + handler registration        |
| `datagen { }`            | Data generator providers                           |

Early compiler versions may implement these as ordinary library functions + builders. Later versions can add dedicated syntax support and stronger static checking.

## 10. Error Messages (Examples)

- “Component `inventory` is not supported on Bedrock target”
- “Identifier `example:CoolStone` should be lowercase”
- “Missing `actual` implementation for Edition.Bedrock”
- “`onEach` handler should avoid allocations in the hot path” (warning)

## 11. Open Questions

- Exact syntax for multi-block / multi-item variants
- How much of the Bedrock component system to surface directly
- Integration with existing Fabric/Forge registries vs fully custom model
- Degree of compile-time validation for recipes, models, and tags

---

This document will evolve as the design solidifies and real prototypes are built.