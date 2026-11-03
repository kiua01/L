package l.compiler

/**
 * Recursive-descent parser prototype for L.
 * Supports a useful subset: package, imports, annotations,
 * fun/val/var, class/object/interface/enum, nested members.
 */
class Parser(
    private val tokens: List<Token>,
    private val diagnostics: DiagnosticBag = DiagnosticBag()
) {
    private var current = 0

    fun parseFile(): AstNode.File {
        val start = pos()
        var packageName: String? = null
        val imports = mutableListOf<Import>()
        val decls = mutableListOf<Decl>()

        if (match(TokenType.PACKAGE)) {
            packageName = parseQualifiedName()
            match(TokenType.SEMICOLON)
        }

        while (match(TokenType.IMPORT)) {
            val path = parseQualifiedNameWithStar()
            var alias: String? = null
            if (match(TokenType.AS)) alias = consumeIdentifier()
            match(TokenType.SEMICOLON)
            imports += Import(path, alias)
        }

        while (!isAtEnd()) {
            val decl = parseDeclaration()
            if (decl != null) decls += decl
            else if (!isAtEnd()) {
                val t = peek()
                diagnostics.error("Unexpected token '${t.lexeme}'", t.pos)
                advance()
            }
        }

        return AstNode.File(packageName, imports, decls, range(start, pos()))
    }

    private fun parseDeclaration(): Decl? {
        val annotations = mutableListOf<String>()
        while (check(TokenType.AT)) {
            annotations += parseAnnotation()
        }

        val modifiers = mutableListOf<String>()
        while (isModifier(peek().type)) {
            modifiers += advance().lexeme
        }

        return when {
            match(TokenType.FUN) -> parseFunction(annotations, modifiers)
            match(TokenType.VAL) -> parseProperty(false, modifiers)
            match(TokenType.VAR) -> parseProperty(true, modifiers)
            match(TokenType.CLASS) -> parseClassLike("class", annotations, modifiers)
            match(TokenType.OBJECT) -> parseClassLike("object", annotations, modifiers)
            match(TokenType.INTERFACE) -> parseClassLike("interface", annotations, modifiers)
            match(TokenType.ENUM) -> {
                match(TokenType.CLASS) // enum class
                parseClassLike("enum", annotations, modifiers)
            }
            match(TokenType.DATA) -> {
                when {
                    match(TokenType.CLASS) -> parseClassLike("data class", annotations, modifiers)
                    match(TokenType.OBJECT) -> parseClassLike("data object", annotations, modifiers)
                    else -> {
                        diagnostics.error("Expected class or object after 'data'", peek().pos)
                        null
                    }
                }
            }
            match(TokenType.SEALED) -> {
                when {
                    match(TokenType.CLASS) -> parseClassLike("sealed class", annotations, modifiers)
                    match(TokenType.INTERFACE) -> parseClassLike("sealed interface", annotations, modifiers)
                    else -> {
                        diagnostics.error("Expected class or interface after 'sealed'", peek().pos)
                        null
                    }
                }
            }
            check(TokenType.IDENTIFIER) && peek().lexeme == "typealias" -> {
                advance()
                val name = consumeIdentifier()
                match(TokenType.ASSIGN)
                val type = parseTypeName()
                Decl.TypeAlias(name, type)
            }
            else -> null
        }
    }

    private fun parseAnnotation(): String {
        match(TokenType.AT)
        val name = when {
            check(TokenType.IDENTIFIER) || check(TokenType.COMPOSABLE) || check(TokenType.PAGE) ->
                advance().lexeme
            else -> {
                diagnostics.error("Expected annotation name", peek().pos)
                "<unknown>"
            }
        }
        if (match(TokenType.LPAREN)) {
            var depth = 1
            while (!isAtEnd() && depth > 0) {
                when (advance().type) {
                    TokenType.LPAREN -> depth++
                    TokenType.RPAREN -> depth--
                    else -> {}
                }
            }
        }
        return name
    }

    private fun parseFunction(annotations: List<String>, modifiers: List<String>): Decl.Function {
        val start = previous().pos
        val name = consumeIdentifier()
        val params = mutableListOf<Param>()

        if (match(TokenType.LPAREN)) {
            if (!check(TokenType.RPAREN)) {
                do {
                    val pname = consumeIdentifier()
                    var ptype: String? = null
                    if (match(TokenType.COLON)) ptype = parseTypeName()
                    params += Param(pname, ptype)
                } while (match(TokenType.COMMA))
            }
            expect(TokenType.RPAREN, ")")
        }

        var returnType: String? = null
        if (match(TokenType.COLON)) returnType = parseTypeName()

        val body = when {
            match(TokenType.LBRACE) -> {
                skipBlockContents()
                Body.Block
            }
            match(TokenType.ASSIGN) -> {
                skipExpressionRoughly()
                Body.Expression
            }
            else -> null
        }

        return Decl.Function(name, params, returnType, body, annotations, modifiers, range(start, pos()))
    }

    private fun parseProperty(mutable: Boolean, modifiers: List<String>): Decl.Property {
        val start = previous().pos
        val name = consumeIdentifier()
        var type: String? = null
        if (match(TokenType.COLON)) type = parseTypeName()
        val hasInit = match(TokenType.ASSIGN)
        if (hasInit) skipExpressionRoughly()
        // skip optional accessors roughly
        while (check(TokenType.IDENTIFIER) && (peek().lexeme == "get" || peek().lexeme == "set")) {
            advance()
            if (match(TokenType.LPAREN)) {
                while (!isAtEnd() && !match(TokenType.RPAREN)) advance()
            }
            if (match(TokenType.LBRACE)) skipBlockContents()
            else if (match(TokenType.ASSIGN)) skipExpressionRoughly()
        }
        return Decl.Property(mutable, name, type, hasInit, modifiers, range(start, pos()))
    }

    private fun parseClassLike(
        kind: String,
        annotations: List<String>,
        modifiers: List<String>
    ): Decl.ClassLike {
        val start = previous().pos
        val name = consumeIdentifier()

        // skip type params <...>
        if (match(TokenType.LT)) skipUntil(TokenType.GT)

        // skip primary constructor
        if (match(TokenType.LPAREN)) {
            var depth = 1
            while (!isAtEnd() && depth > 0) {
                when (advance().type) {
                    TokenType.LPAREN -> depth++
                    TokenType.RPAREN -> depth--
                    else -> {}
                }
            }
        }

        // skip supertypes
        if (match(TokenType.COLON)) {
            while (!isAtEnd() && !check(TokenType.LBRACE) && !isDeclarationStart()) {
                advance()
            }
        }

        val members = mutableListOf<Decl>()
        if (match(TokenType.LBRACE)) {
            while (!isAtEnd() && !check(TokenType.RBRACE)) {
                val member = parseDeclaration()
                if (member != null) members += member
                else advance()
            }
            expect(TokenType.RBRACE, "}")
        }

        return Decl.ClassLike(kind, name, members, annotations, modifiers, range(start, pos()))
    }

    private fun parseTypeName(): String {
        val parts = mutableListOf<String>()
        // nullable / simple / qualified / generic rough parse
        if (match(TokenType.LPAREN)) {
            parts += "("
            while (!isAtEnd() && !check(TokenType.RPAREN)) {
                parts += advance().lexeme
            }
            match(TokenType.RPAREN)
            parts += ")"
            if (match(TokenType.ARROW)) {
                parts += "->"
                parts += parseTypeName()
            }
        } else {
            parts += consumeIdentifier()
            while (match(TokenType.DOT)) {
                parts += "."
                parts += consumeIdentifier()
            }
            if (match(TokenType.LT)) {
                parts += "<"
                while (!isAtEnd() && !check(TokenType.GT)) {
                    parts += advance().lexeme
                }
                match(TokenType.GT)
                parts += ">"
            }
        }
        if (match(TokenType.QUEST)) parts += "?"
        return parts.joinToString("")
    }

    private fun parseQualifiedName(): String {
        val parts = mutableListOf(consumeIdentifier())
        while (match(TokenType.DOT)) parts += consumeIdentifier()
        return parts.joinToString(".")
    }

    private fun parseQualifiedNameWithStar(): String {
        val parts = mutableListOf(consumeIdentifier())
        while (match(TokenType.DOT)) {
            if (match(TokenType.STAR)) {
                parts += "*"
                break
            }
            parts += consumeIdentifier()
        }
        return parts.joinToString(".")
    }

    private fun skipBlockContents() {
        var depth = 1
        while (!isAtEnd() && depth > 0) {
            when (advance().type) {
                TokenType.LBRACE -> depth++
                TokenType.RBRACE -> depth--
                else -> {}
            }
        }
    }

    private fun skipExpressionRoughly() {
        var depthParen = 0
        var depthBrace = 0
        var depthBracket = 0
        while (!isAtEnd()) {
            when (peek().type) {
                TokenType.LPAREN -> { depthParen++; advance() }
                TokenType.RPAREN -> {
                    if (depthParen == 0 && depthBrace == 0 && depthBracket == 0) break
                    depthParen--; advance()
                }
                TokenType.LBRACE -> { depthBrace++; advance() }
                TokenType.RBRACE -> {
                    if (depthBrace == 0 && depthParen == 0 && depthBracket == 0) break
                    depthBrace--; advance()
                }
                TokenType.LBRACKET -> { depthBracket++; advance() }
                TokenType.RBRACKET -> {
                    if (depthBracket == 0 && depthParen == 0 && depthBrace == 0) break
                    depthBracket--; advance()
                }
                TokenType.COMMA, TokenType.SEMICOLON ->
                    if (depthParen == 0 && depthBrace == 0 && depthBracket == 0) break
                    else advance()
                TokenType.FUN, TokenType.VAL, TokenType.VAR, TokenType.CLASS,
                TokenType.OBJECT, TokenType.INTERFACE, TokenType.ENUM,
                TokenType.DATA, TokenType.SEALED, TokenType.AT, TokenType.EOF ->
                    if (depthParen == 0 && depthBrace == 0 && depthBracket == 0) break
                    else advance()
                else -> advance()
            }
            if (isModifier(peek().type) && depthParen == 0 && depthBrace == 0 && depthBracket == 0) break
        }
    }

    private fun skipUntil(type: TokenType) {
        var depth = 1
        while (!isAtEnd() && depth > 0) {
            val t = advance().type
            if (t == TokenType.LT) depth++
            if (t == type) depth--
        }
    }

    private fun isDeclarationStart(): Boolean =
        check(TokenType.FUN) || check(TokenType.VAL) || check(TokenType.VAR) ||
            check(TokenType.CLASS) || check(TokenType.OBJECT) || check(TokenType.INTERFACE) ||
            check(TokenType.ENUM) || check(TokenType.DATA) || check(TokenType.SEALED) ||
            check(TokenType.AT) || isModifier(peek().type)

    private fun isModifier(type: TokenType): Boolean = type in setOf(
        TokenType.PRIVATE, TokenType.PROTECTED, TokenType.PUBLIC, TokenType.INTERNAL,
        TokenType.OPEN, TokenType.ABSTRACT, TokenType.FINAL, TokenType.OVERRIDE,
        TokenType.INLINE, TokenType.SUSPEND, TokenType.EXPECT, TokenType.ACTUAL,
        TokenType.VALUE, TokenType.DATA, TokenType.SEALED
    )

    private fun consumeIdentifier(): String {
        val t = peek()
        if (t.type == TokenType.IDENTIFIER || t.lexeme.isNotEmpty() && t.type != TokenType.EOF) {
            advance()
            return t.lexeme
        }
        diagnostics.error("Expected identifier", t.pos)
        return "<unknown>"
    }

    private fun expect(type: TokenType, label: String) {
        if (!match(type)) diagnostics.error("Expected '$label'", peek().pos)
    }

    private fun match(type: TokenType): Boolean {
        if (check(type)) {
            advance()
            return true
        }
        return false
    }

    private fun check(type: TokenType): Boolean = !isAtEnd() && peek().type == type
    private fun advance(): Token {
        if (!isAtEnd()) current++
        return tokens[current - 1]
    }
    private fun previous(): Token = tokens[current - 1]
    private fun peek(): Token = tokens[current]
    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
    private fun pos(): SourcePos = if (current < tokens.size) peek().pos else tokens.last().pos
    private fun range(start: SourcePos, end: SourcePos) = SourceRange(start, end)
}