package l.compiler

import java.io.File

/** Shared front-end pipeline used by CLI commands. */
object CompilerPipeline {
    data class Result(
        val ast: AstNode.File,
        val diagnostics: DiagnosticBag,
        val symbols: List<Resolver.Symbol>,
        val minecraft: MinecraftDslLowerer.Result? = null
    )

    fun analyze(source: String): Result {
        val diagnostics = DiagnosticBag()
        val tokens = Lexer(source).tokenize()
        val ast = Parser(tokens, diagnostics).parseFile()
        val symbols = Resolver(diagnostics).resolve(ast)
        TypeChecker(diagnostics).check(ast)
        val mc = MinecraftDslLowerer(diagnostics).analyze(ast)
        return Result(ast, diagnostics, symbols, mc)
    }

    fun analyzeFile(path: String): Result = analyze(File(path).readText())

    fun compileFile(path: String, outDir: File, className: String): Result {
        val result = analyzeFile(path)
        if (!result.diagnostics.hasErrors) {
            JvmBackend().emit(result.ast, className, outDir)
        }
        return result
    }
}