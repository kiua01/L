package l.compiler

enum class Severity { ERROR, WARNING, INFO }

data class Diagnostic(
    val severity: Severity,
    val message: String,
    val pos: SourcePos?
) {
    override fun toString(): String {
        val loc = pos?.let { "${it.line}:${it.column}: " } ?: ""
        return "$loc${severity.name.lowercase()}: $message"
    }
}

class DiagnosticBag {
    private val items = mutableListOf<Diagnostic>()

    val diagnostics: List<Diagnostic> get() = items
    val hasErrors: Boolean get() = items.any { it.severity == Severity.ERROR }

    fun error(message: String, pos: SourcePos? = null) {
        items += Diagnostic(Severity.ERROR, message, pos)
    }

    fun warning(message: String, pos: SourcePos? = null) {
        items += Diagnostic(Severity.WARNING, message, pos)
    }

    fun info(message: String, pos: SourcePos? = null) {
        items += Diagnostic(Severity.INFO, message, pos)
    }

    fun printAll() {
        items.forEach { println(it) }
    }
}