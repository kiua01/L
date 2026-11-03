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
            val diagnostics = DiagnosticBag()
            val ast = Parser(Lexer(File(path).readText()).tokenize(), diagnostics).parseFile()
            println("package: ${ast.packageName ?: "<none>"}")
            if (ast.imports.isNotEmpty()) {
                println("imports:")
                ast.imports.forEach { println("  ${it.path}") }
            }
            println("declarations: ${ast.declarations.size}")
            printDecls(ast.declarations, "  ")
            if (diagnostics.diagnostics.isNotEmpty()) {
                println("\ndiagnostics:")
                diagnostics.printAll()
            }
        }
        "resolve" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val diagnostics = DiagnosticBag()
            val ast = Parser(Lexer(File(path).readText()).tokenize(), diagnostics).parseFile()
            val symbols = Resolver(diagnostics).resolve(ast)
            println("symbols (${symbols.size}):")
            symbols.forEach { s ->
                println("  ${s.kind}\t${s.owner?.let { "$it." } ?: ""}${s.name}")
            }
            if (diagnostics.diagnostics.isNotEmpty()) {
                println("\ndiagnostics:")
                diagnostics.printAll()
            }
        }
        "check" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val diagnostics = DiagnosticBag()
            val ast = Parser(Lexer(File(path).readText()).tokenize(), diagnostics).parseFile()
            Resolver(diagnostics).resolve(ast)
            TypeChecker(diagnostics).check(ast)
            if (diagnostics.hasErrors) {
                diagnostics.printAll()
                kotlin.system.exitProcess(1)
            } else {
                println("OK — no errors")
                diagnostics.printAll()
            }
        }
        "compile" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val outDir = File(args.getOrNull(2) ?: "out")
            val className = args.getOrNull(3) ?: "Main"
            val diagnostics = DiagnosticBag()
            val source = File(path).readText()
            val ast = Parser(Lexer(source).tokenize(), diagnostics).parseFile()
            Resolver(diagnostics).resolve(ast)
            TypeChecker(diagnostics).check(ast)
            if (diagnostics.hasErrors) {
                diagnostics.printAll()
                kotlin.system.exitProcess(1)
            }
            JvmBackend().emit(ast, className, outDir)
            println("Wrote $outDir/$className.class")
            println("Run: java -cp $outDir $className")
        }
        "version" -> println("L compiler prototype 0.0.3")
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
        |L compiler prototype 0.0.3
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