package l.compiler

/** Minimal AST for the prototype. Will expand significantly. */
sealed class AstNode {
    data class File(
        val packageName: String?,
        val declarations: List<Decl>
    ) : AstNode()
}

sealed class Decl : AstNode() {
    data class Function(
        val name: String,
        val params: List<String>,
        val bodyPresent: Boolean
    ) : Decl()

    data class Property(
        val mutable: Boolean,
        val name: String
    ) : Decl()

    data class ClassLike(
        val kind: String, // class / object / interface
        val name: String
    ) : Decl()

    data class Unknown(val raw: String) : Decl()
}