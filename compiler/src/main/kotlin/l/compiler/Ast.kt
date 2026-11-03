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
        val initializer: Expr?,
        val modifiers: List<String> = emptyList(),
        override val range: SourceRange? = null
    ) : Decl()

    data class ClassLike(
        val kind: String,
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
    data class Block(val statements: List<Stmt>) : Body()
    data class Expression(val expr: Expr) : Body()
}

sealed class Stmt {
    data class ExprStmt(val expr: Expr) : Stmt()
    data class Return(val expr: Expr?) : Stmt()
    data class LocalVar(val mutable: Boolean, val name: String, val type: String?, val init: Expr?) : Stmt()
    data object Empty : Stmt()
}

sealed class Expr {
    data class Literal(val value: String, val kind: LitKind) : Expr()
    data class Ident(val name: String) : Expr()
    data class Binary(val left: Expr, val op: String, val right: Expr) : Expr()
    data class Unary(val op: String, val expr: Expr) : Expr()
    data class Call(val callee: Expr, val args: List<Expr>) : Expr()
    data class Member(val receiver: Expr, val name: String) : Expr()
    data class Index(val receiver: Expr, val index: Expr) : Expr()
    data class Assign(val target: Expr, val value: Expr) : Expr()
    data class StringTemplate(val parts: List<String>) : Expr()
    data object Unknown : Expr()
}

enum class LitKind { INT, FLOAT, STRING, CHAR, BOOL, NULL }