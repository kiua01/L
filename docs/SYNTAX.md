# L Syntax Overview

L has **full Kotlin syntax**. The examples below show both standard Kotlin-style code and planned Minecraft-oriented extensions.

## Basic Kotlin Parity Examples

```l
// Variables
val name: String = "L"
var count = 0

// Null safety
val maybe: String? = null
val length = maybe?.length ?: 0

// Data class
data class Vec3(val x: Double, val y: Double, val z: Double)

// Sealed hierarchy
sealed interface Result<out T> {
    data class Ok<T>(val value: T) : Result<T>
    data class Err(val message: String) : Result<Nothing>
}

// Coroutines
suspend fun loadWorld(name: String): World {
    return withContext(Dispatchers.IO) {
        // ...
    }
}

// Extension functions
fun String.toIdentifier(): Identifier = Identifier(this)
```

## Planned Minecraft DSL Examples (Vision)

> These are **not final**. They illustrate the intended ergonomics.

### Registry

```l
@Mod("example")
object ExampleMod {

    val BLOCKS = registry<Block> {
        "cool_stone" {
            settings {
                mapColor = MapColor.STONE
                hardness = 1.5f
                resistance = 6.0f
            }
        }

        "glowing_ore" {
            settings {
                luminance = 10
                // ...
            }
        }
    }

    val ITEMS = registry<Item> {
        "cool_stone" {
            block = BLOCKS["cool_stone"]
        }
    }
}
```

### Events

```l
on<ServerLifecycleEvents.ServerStarted> { server ->
    logger.info("Server started with L!")
}

onEach<ServerTickEvent> { event ->
    // runs every tick – keep allocation free
}
```

### Networking

```l
packet<HelloPacket>(id = "example:hello") {
    codec {
        string("message")
    }

    serverHandler { packet, context ->
        context.player.sendMessage(Text.literal(packet.message))
    }
}
```

### Identifiers

```l
val id = id("example", "cool_stone")   // compile-time friendly
val same = "example:cool_stone".id
```

## File Extension

**All L source files use the `.L` extension** (uppercase).

Examples:
- `Main.L`
- `RobotEntity.L`
- `HomeScreen.L`
- `LauncherCore.L`

One language. One extension. Everything.

## Comments

Same as Kotlin:
```l
// single line
/* multi
   line */
```