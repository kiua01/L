// examples/launcher-core/LauncherCore.L
// Vision example of shared launcher logic (PC + Android)

expect object Platform {
    fun getGameDir(): Path
    fun getCacheDir(): Path
    suspend fun download(url: String, target: Path)
}

class InstanceManager {

    private val instances = mutableListOf<Instance>()

    suspend fun createInstance(
        name: String,
        version: GameVersion,
        modLoader: ModLoader? = null
    ): Instance {
        val dir = Platform.getGameDir() / "instances" / name
        dir.createDirectories()

        // Download client jar, libraries, assets...
        // This logic can be shared between PC and Android

        val instance = Instance(name, version, modLoader, dir)
        instances += instance
        return instance
    }

    fun listInstances(): List<Instance> = instances.toList()
}

data class Instance(
    val name: String,
    val version: GameVersion,
    val modLoader: ModLoader?,
    val directory: Path
)

enum class ModLoader {
    VANILLA, FABRIC, FORGE, NEOFORGE, QUILT
}