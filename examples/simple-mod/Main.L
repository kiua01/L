// examples/simple-mod/Main.L
// Vision example — not compilable yet

@Mod("simple_l_mod")
object SimpleLMod {

    private val LOGGER = logger()

    val BLOCKS = registry<Block> {
        "demo_block" {
            settings {
                mapColor = MapColor.STONE
                hardness = 2.0f
                resistance = 6.0f
                requiresTool = true
            }
        }
    }

    val ITEMS = registry<Item> {
        "demo_block" {
            // Automatically creates a BlockItem for the block above
            fromBlock(BLOCKS["demo_block"])
        }
    }

    init {
        on<ServerLifecycleEvents.ServerStarted> {
            LOGGER.info("Simple L Mod loaded successfully!")
        }
    }
}