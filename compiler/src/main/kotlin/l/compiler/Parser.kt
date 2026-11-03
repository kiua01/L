package l.compiler

/**
 * Extremely small parser prototype.
 * Goal: prove pipeline File -> Tokens -> partial AST.
 * Not a complete L parser yet.
 */
class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parseFile(): AstNode.File {
        var packageName: String? = null
        val decls = mutableListOf<Decl>()

        if (match(TokenType.PACKAGE)) {
            packageName = parseQualifiedName()
        }

        while (!isAtEnd()) {
            when {
                match(TokenType.FUN) -> decls += parseFunction()
                match(TokenType.VAL) -> decls += Decl.Property(false, consumeIdentifier())
                match(TokenType.VAR) -> decls += Decl.Property(true, consumeIdentifier())
                match(TokenType.CLASS) -> decls += Decl.ClassLike("class", consumeIdentifier())
                match(TokenType.OBJECT) -> decls += Decl.ClassLike("object", consumeIdentifier())
                match(TokenType.INTERFACE) -> decls += Decl.ClassLike("interface", consumeIdentifier())
                match(TokenType.AT) -> {
                    // skip annotation for now
                    if (check(TokenType.IDENTIFIER) || check(TokenType.COMPOSABLE) || check(TokenType.PAGE)) advance()
                    if (match(TokenType.LPAREN)) {
                        while (!isAtEnd() && !match(TokenType.RPAREN)) advance()
                    }
                }
                else -> {
                    // skip unknown token to avoid infinite loop
                    advance()
                }
            }
        }

        return AstNode.File(packageName, decls)
    }

    private fun parseFunction(): Decl.Function {
        val name = consumeIdentifier()
        val params = mutableListOf<String>()
        if (match(TokenType.LPAREN)) {
            if (!check(TokenType.RPAREN)) {
                do {
                    params += consumeIdentifier()
                    // skip type annotations roughly: : Type
                    if (match(TokenType.COLON)) {
                        while (!isAtEnd() && !check(TokenType.COMMA) && !check(TokenType.RPAREN)) advance()
                    }
                } while (match(TokenType.COMMA))
            }
            match(TokenType.RPAREN)
        }
        // skip return type
        if (match(TokenType.COLON)) {
            while (!isAtEnd() && !check(TokenType.LBRACE) && !check(TokenType.ASSIGN)) advance()
        }
        val bodyPresent = check(TokenType.LBRACE) || check(TokenType.ASSIGN)
        // skip body roughly
        if (match(TokenType.LBRACE)) skipBlock()
        else if (match(TokenType.ASSIGN)) {
            while (!isAtEnd() && !check(TokenType.FUN) && !check(TokenType.VAL) && !check(TokenType.VAR)
                && !check(TokenType.CLASS) && !check(TokenType.OBJECT) && !check(TokenType.EOF)
            ) advance()
        }
        return Decl.Function(name, params, bodyPresent)
    }

    private fun skipBlock() {
        var depth = 1
        while (!isAtEnd() && depth > 0) {
            when (advance().type) {
                TokenType.LBRACE -> depth++
                TokenType.RBRACE -> depth--
                else -> {}
            }
        }
    }

    private fun parseQualifiedName(): String {
        val parts = mutableListOf(consumeIdentifier())
        while (match(TokenType.DOT)) parts += consumeIdentifier()
        return parts.joinToString(".")
    }

    private fun consumeIdentifier(): String {
        val t = advance()
        return if (t.type == TokenType.IDENTIFIER || t.lexeme.isNotEmpty()) t.lexeme else "<unknown>"
    }

    private fun match(type: TokenType): Boolean {
        if (check(type)) {
            advance()
            return true
        }
        return false
    }

    private fun check(type: TokenType): Boolean =
        !isAtEnd() && peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return tokens[current - 1]
    }

    private fun peek(): Token = tokens[current]
    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
}