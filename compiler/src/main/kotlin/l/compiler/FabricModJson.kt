package l.compiler

import java.io.File

/**
 * Generates a minimal fabric.mod.json for L Fabric projects.
 * Bear loader support will be a separate generator later.
 */
object FabricModJson {
    data class Spec(
        val id: String,
        val name: String,
        val version: String = "0.1.0",
        val description: String = "L Fabric mod",
        val mainClass: String = "Main",
        val authors: List<String> = listOf("L Developer"),
        val fabricApi: Boolean = true
    )

    fun generate(spec: Spec): String {
        val authors = spec.authors.joinToString(",") { "\"$it\"" }
        val depends = buildString {
            append("\"fabricloader\": \">=0.15.0\"")
            append(",\n    \"minecraft\": \">=1.20\"")
            if (spec.fabricApi) append(",\n    \"fabric-api\": \"*\"")
        }
        return """
            |{
            |  "schemaVersion": 1,
            |  "id": "${spec.id}",
            |  "version": "${spec.version}",
            |  "name": "${spec.name}",
            |  "description": "${spec.description}",
            |  "authors": [$authors],
            |  "environment": "*",
            |  "entrypoints": {
            |    "main": [
            |      "${spec.mainClass}"
            |    ]
            |  },
            |  "depends": {
            |    $depends
            |  },
            |  "custom": {
            |    "l:language": "L",
            |    "l:loader_target": "fabric",
            |    "l:future_loader": "bear"
            |  }
            |}
        """.trimMargin()
    }

    fun write(spec: Spec, dir: File) {
        dir.mkdirs()
        File(dir, "fabric.mod.json").writeText(generate(spec))
    }
}