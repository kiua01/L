package l.compiler

/**
 * Fabric-oriented Minecraft DSL analysis.
 * Forge/NeoForge are out of scope for now.
 * Future: Bear loader will get its own lowerer.
 */
class MinecraftDslLowerer(private val diagnostics: DiagnosticBag) {

    data class RegistryDecl(val name: String, val kind: String)
    data class EntityDecl(val name: String)
    data class PacketDecl(val name: String)
    data class EntrypointDecl(val className: String, val method: String)

    data class Result(
        val target: String = "fabric",
        val registries: List<RegistryDecl>,
        val entities: List<EntityDecl>,
        val packets: List<PacketDecl>,
        val entrypoints: List<EntrypointDecl>
    )

    fun analyze(file: AstNode.File): Result {
        val registries = mutableListOf<RegistryDecl>()
        val entities = mutableListOf<EntityDecl>()
        val packets = mutableListOf<PacketDecl>()
        val entrypoints = mutableListOf<EntrypointDecl>()

        for (d in file.declarations) {
            when (d) {
                is Decl.Function -> {
                    when (d.name) {
                        "onInitialize", "on_initialize" ->
                            entrypoints += EntrypointDecl("Main", d.name)
                        "entity", "ent" -> entities += EntityDecl(d.name)
                        "packet", "pkt" -> packets += PacketDecl(d.name)
                        else -> {}
                    }
                }
                is Decl.Property -> {
                    val n = d.name
                    when {
                        n.contains("Block", true) || n.contains("BLOCKS", true) ->
                            registries += RegistryDecl(n, "block")
                        n.contains("Item", true) || n.contains("ITEMS", true) ->
                            registries += RegistryDecl(n, "item")
                        n.contains("REGISTRY", true) || n.contains("Reg", true) ->
                            registries += RegistryDecl(n, "registry")
                    }
                }
                is Decl.ClassLike -> {
                    val methods = d.members.filterIsInstance<Decl.Function>().map { it.name }
                    if (methods.any { it == "onInitialize" || it == "on_initialize" }) {
                        entrypoints += EntrypointDecl(d.name, "onInitialize")
                    }
                    if (d.name.contains("Entity", true)) {
                        entities += EntityDecl(d.name)
                    }
                }
                else -> {}
            }
        }
        return Result(
            target = "fabric",
            registries = registries,
            entities = entities,
            packets = packets,
            entrypoints = entrypoints
        )
    }
}