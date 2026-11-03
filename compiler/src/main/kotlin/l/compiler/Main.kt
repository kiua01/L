package l.compiler

import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        return
    }

    when (args[0]) {
        "lex" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            Lexer(File(path).readText()).tokenize().forEach { t ->
                if (t.type != TokenType.EOF)
                    println("${t.pos.line}:${t.pos.column}\t${t.type}\t${t.lexeme}")
            }
        }
        "parse" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val r = CompilerPipeline.analyzeFile(path)
            val ast = r.ast
            println("package: ${ast.packageName ?: "<none>"}")
            if (ast.imports.isNotEmpty()) {
                println("imports:")
                ast.imports.forEach { println("  ${it.path}") }
            }
            println("declarations: ${ast.declarations.size}")
            printDecls(ast.declarations, "  ")
            if (r.diagnostics.diagnostics.isNotEmpty()) {
                println("\ndiagnostics:")
                r.diagnostics.printAll()
            }
        }
        "resolve" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val r = CompilerPipeline.analyzeFile(path)
            println("symbols (${r.symbols.size}):")
            r.symbols.forEach { s ->
                println("  ${s.kind}\t${s.owner?.let { "$it." } ?: ""}${s.name}")
            }
            if (r.diagnostics.diagnostics.isNotEmpty()) {
                println("\ndiagnostics:")
                r.diagnostics.printAll()
            }
        }
        "check" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val r = CompilerPipeline.analyzeFile(path)
            if (r.diagnostics.hasErrors) {
                r.diagnostics.printAll()
                kotlin.system.exitProcess(1)
            } else {
                println("OK — no errors")
                r.diagnostics.printAll()
            }
        }
        "compile" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val outDir = File(args.getOrNull(2) ?: "out")
            val className = args.getOrNull(3) ?: "Main"
            val r = CompilerPipeline.compileFile(path, outDir, className)
            if (r.diagnostics.hasErrors) {
                r.diagnostics.printAll()
                kotlin.system.exitProcess(1)
            }
            println("Wrote $outDir/$className.class")
            println("Run: java -cp $outDir $className")
        }
        "version" -> println("L compiler prototype 0.0.9")
        else -> printUsage()
    }
}

private fun printDecls(decls: List<Decl>, indent: String) {
    for (d in decls) {
        when (d) {
            is Decl.Function -> {
                val params = d.params.joinToString { "${it.name}${it.type?.let { t -> ": $t" } ?: ""}" }
                println("${indent}fun ${d.name}($params)${d.returnType?.let { ": $it" } ?: ""}")
            }
            is Decl.Property -> println("${indent}${if (d.mutable) "var" else "val"} ${d.name}${d.type?.let { ": $it" } ?: ""}")
            is Decl.ClassLike -> {
                println("${indent}${d.kind} ${d.name}")
                printDecls(d.members, "$indent  ")
            }
            is Decl.TypeAlias -> println("${indent}typealias ${d.name}")
        }
    }
}

private fun printUsage() {
    println(
        """
        |L compiler prototype 0.0.9
        |
        |  l lex <file>
        |  l parse <file>
        |  l resolve <file>
        |  l check <file>
        |  l compile <file> [outDir] [ClassName]
        |  l version
        """.trimMargin()
    )
}