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
            val source = File(path).readText()
            val tokens = Lexer(source).tokenize()
            tokens.forEach { t ->
                if (t.type != TokenType.EOF) {
                    println("${t.pos.line}:${t.pos.column}\t${t.type}\t${t.lexeme}")
                }
            }
        }
        "parse" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val source = File(path).readText()
            val diagnostics = DiagnosticBag()
            val tokens = Lexer(source).tokenize()
            val ast = Parser(tokens, diagnostics).parseFile()

            println("package: ${ast.packageName ?: "<none>"}")
            if (ast.imports.isNotEmpty()) {
                println("imports:")
                ast.imports.forEach { println("  ${it.path}${it.alias?.let { a -> " as $a" } ?: ""}") }
            }
            println("declarations: ${ast.declarations.size}")
            printDecls(ast.declarations, "  ")

            if (diagnostics.diagnostics.isNotEmpty()) {
                println()
                println("diagnostics:")
                diagnostics.printAll()
            }
        }
        "resolve" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val source = File(path).readText()
            val diagnostics = DiagnosticBag()
            val tokens = Lexer(source).tokenize()
            val ast = Parser(tokens, diagnostics).parseFile()
            val symbols = Resolver(diagnostics).resolve(ast)

            println("symbols (${symbols.size}):")
            symbols.forEach { s ->
                val owner = s.owner?.let { "$it." } ?: ""
                println("  ${s.kind}\t$owner${s.name}")
            }
            if (diagnostics.diagnostics.isNotEmpty()) {
                println()
                println("diagnostics:")
                diagnostics.printAll()
            }
        }
        "check" -> {
            val path = args.getOrNull(1) ?: return printUsage()
            val source = File(path).readText()
            val diagnostics = DiagnosticBag()
            val tokens = Lexer(source).tokenize()
            val ast = Parser(tokens, diagnostics).parseFile()
            Resolver(diagnostics).resolve(ast)
            if (diagnostics.hasErrors) {
                diagnostics.printAll()
                kotlin.system.exitProcess(1)
            } else {
                println("OK — no errors (${ast.declarations.size} top-level declarations)")
                diagnostics.printAll()
            }
        }
        "version" -> println("L compiler prototype 0.0.2")
        else -> printUsage()
    }
}

private fun printDecls(decls: List<Decl>, indent: String) {
    for (d in decls) {
        when (d) {
            is Decl.Function -> {
                val params = d.params.joinToString { p ->
                    p.name + (p.type?.let { ": $it" } ?: "")
                }
                val ret = d.returnType?.let { ": $it" } ?: ""
                val anns = if (d.annotations.isNotEmpty()) "@${d.annotations.joinToString(",@")} " else ""
                println("${indent}${anns}fun ${d.name}($params)$ret")
            }
            is Decl.Property -> {
                val kw = if (d.mutable) "var" else "val"
                val ty = d.type?.let { ": $it" } ?: ""
                println("${indent}$kw ${d.name}$ty")
            }
            is Decl.ClassLike -> {
                val anns = if (d.annotations.isNotEmpty()) "@${d.annotations.joinToString(",@")} " else ""
                println("${indent}${anns}${d.kind} ${d.name}")
                if (d.members.isNotEmpty()) printDecls(d.members, "$indent  ")
            }
            is Decl.TypeAlias -> println("${indent}typealias ${d.name}")
        }
    }
}

private fun printUsage() {
    println(
        """
        |L compiler prototype 0.0.2
        |
        |Usage:
        |  l lex <file>       Tokenize
        |  l parse <file>     Parse to AST
        |  l resolve <file>   Parse + collect symbols
        |  l check <file>     Parse + resolve, exit 1 on errors
        |  l version          Show version
        |
        |Files: .L (general) or .le (web)
        """.trimMargin()
    )
}