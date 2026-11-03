// examples/cross-edition/RobotEntity.l
// Vision: one definition that can target both Java Edition and Bedrock

@Target(Edition.Java, Edition.Bedrock)
entity("demo:robot") {

    // High-level components — compiler lowers them appropriately
    components {
        health(max = 30)
        movement(speed = 0.28)
        attack(damage = 4, range = 2.5)
        inventory(size = 9)

        // Visuals
        model("demo:robot")
        texture("demo:robot")
    }

    // Behavior described once
    events {
        onSpawn {
            playSound("demo:robot_activate")
        }

        onInteract { player ->
            player.sendMessage("Beep boop — hello from L!")
            // This call is mapped to the correct edition API
        }

        onHurt { attacker, amount ->
            if (health.current < 10) {
                // Low health behavior
                playSound("demo:robot_alarm")
                spawnParticle("demo:smoke", at = position)
            }
        }
    }

    // Edition-specific overrides when needed
    actual(Edition.Java) {
        // Can use Java-only features or more precise control
        mixins {
            // ...
        }
    }

    actual(Edition.Bedrock) {
        // Can add Bedrock-specific component tweaks
        molang {
            // ...
        }
    }
}