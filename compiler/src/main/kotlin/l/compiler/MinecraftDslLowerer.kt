package l.compiler

/**
 * Early Minecraft DSL recognition / lowering hooks.
 * Full Fabric/NeoForge emission comes later; this pass records DSL shapes.
 */
class MinecraftDslLowerer(private val diagnostics: DiagnosticBag) {

    data class RegistryDecl(val typeArg: String?, val entries: List<String>)
    data class EntityDecl(val name: String)
    data class PacketDecl(val name: String)

    data class Result(
        val registries: List<RegistryDecl>,
        val entities: List<EntityDecl>,
        val packets: List<PacketDecl>
    )

    fun analyze(file: AstNode.File): Result {
        val registries = mutableListOf<RegistryDecl>()
        val entities = mutableListOf<EntityDecl>()
        val packets = mutableListOf<PacketDecl>()

        // Scan top-level properties/calls in a lightweight way via declaration names
        for (d in file.declarations) {
            when (d) {
                is Decl.Function -> {
                    when (d.name) {
                        "entity", "ent" -> entities += EntityDecl(d.name)
                        "packet", "pkt" -> packets += PacketDecl(d.name)
                        else -> {}
                    }
                }
                is Decl.Property -> {
                    if (d.name.contains("REGISTRY", ignoreCase = true) ||
                        d.name.contains("Blocks", ignoreCase = true) ||
                        d.name.contains("Items", ignoreCase = true)
                    ) {
                        registries += RegistryDecl(null, listOf(d.name))
                    }
                }
                is Decl.ClassLike -> {
                    if (d.name.contains("Entity", ignoreCase = true)) {
                        entities += EntityDecl(d.name)
                    }
                }
                else -> {}
            }
        }
        return Result(registries, entities, packets)
    }
}