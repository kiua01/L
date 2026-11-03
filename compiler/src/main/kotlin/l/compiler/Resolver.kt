package l.compiler

/**
 * Very early name-resolution pass.
 * Collects top-level and nested declarations into a symbol table.
 */
class Resolver(private val diagnostics: DiagnosticBag) {

    data class Symbol(
        val name: String,
        val kind: String,
        val owner: String?
    )

    fun resolve(file: AstNode.File): List<Symbol> {
        val symbols = mutableListOf<Symbol>()
        val seen = mutableSetOf<String>()

        fun add(name: String, kind: String, owner: String?) {
            val key = listOfNotNull(owner, name).joinToString(".")
            if (!seen.add(key)) {
                diagnostics.error("Redeclaration of '$key'")
            } else {
                symbols += Symbol(name, kind, owner)
            }
        }

        fun walk(decls: List<Decl>, owner: String?) {
            for (d in decls) {
                when (d) {
                    is Decl.Function -> {
                        add(d.name, "function", owner)
                    }
                    is Decl.Property -> {
                        add(d.name, if (d.mutable) "var" else "val", owner)
                    }
                    is Decl.ClassLike -> {
                        add(d.name, d.kind, owner)
                        val nextOwner = listOfNotNull(owner, d.name).joinToString(".")
                        walk(d.members, nextOwner)
                    }
                    is Decl.TypeAlias -> {
                        add(d.name, "typealias", owner)
                    }
                }
            }
        }

        walk(file.declarations, file.packageName)
        return symbols
    }
}