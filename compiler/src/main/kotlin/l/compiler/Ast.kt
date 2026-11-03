package l.compiler

data class SourceRange(val start: SourcePos, val end: SourcePos)

sealed class AstNode {
    abstract val range: SourceRange?

    data class File(
        val packageName: String?,
        val imports: List<Import>,
        val declarations: List<Decl>,
        override val range: SourceRange? = null
    ) : AstNode()
}

data class Import(
    val path: String,
    val alias: String? = null,
    val range: SourceRange? = null
)

sealed class Decl : AstNode() {
    data class Function(
        val name: String,
        val params: List<Param>,
        val returnType: String?,
        val body: Body?,
        val annotations: List<String> = emptyList(),
        val modifiers: List<String> = emptyList(),
        override val range: SourceRange? = null
    ) : Decl()

    data class Property(
        val mutable: Boolean,
        val name: String,
        val type: String?,
        val initializer: Boolean,
        val modifiers: List<String> = emptyList(),
        override val range: SourceRange? = null
    ) : Decl()

    data class ClassLike(
        val kind: String, // class / object / interface / enum
        val name: String,
        val members: List<Decl> = emptyList(),
        val annotations: List<String> = emptyList(),
        val modifiers: List<String> = emptyList(),
        override val range: SourceRange? = null
    ) : Decl()

    data class TypeAlias(
        val name: String,
        val type: String?,
        override val range: SourceRange? = null
    ) : Decl()
}

data class Param(
    val name: String,
    val type: String?
)

sealed class Body {
    data object Block : Body()
    data object Expression : Body()
}