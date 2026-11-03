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
            val tokens = Lexer(source).tokenize()
            val ast = Parser(tokens).parseFile()
            println("package: ${ast.packageName ?: "<none>"}")
            println("declarations: ${ast.declarations.size}")
            ast.declarations.forEach { d ->
                when (d) {
                    is Decl.Function -> println("  fun ${d.name}(${d.params.joinToString()})")
                    is Decl.Property -> println("  ${if (d.mutable) "var" else "val"} ${d.name}")
                    is Decl.ClassLike -> println("  ${d.kind} ${d.name}")
                    is Decl.Unknown -> println("  unknown: ${d.raw}")
                }
            }
        }
        "version" -> println("L compiler prototype 0.0.1")
        else -> printUsage()
    }
}

private fun printUsage() {
    println(
        """
        |L compiler prototype
        |
        |Usage:
        |  l lex <file.L|file.le>     Tokenize a source file
        |  l parse <file.L|file.le>   Parse a source file (minimal AST)
        |  l version                  Show version
        |
        |This is an early prototype: lexer works, parser is minimal.
        """.trimMargin()
    )
}