package l.compiler

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
                diagnostics.error("Unexpected token '${peek().lexeme}'", peek().pos)
                advance()
            }
        }
        return AstNode.File(packageName, imports, decls, range(start, pos()))
    }

    private fun parseDeclaration(): Decl? {
        val annotations = mutableListOf<String>()
        while (check(TokenType.AT)) annotations += parseAnnotation()

        val modifiers = mutableListOf<String>()
        while (isModifier(peek().type) && !check(TokenType.DATA) && !check(TokenType.SEALED) && !check(TokenType.VALUE)) {
            modifiers += advance().lexeme
        }
        // allow data/sealed/value as leading modifiers too
        while (check(TokenType.DATA) || check(TokenType.SEALED) || check(TokenType.VALUE) ||
            check(TokenType.INLINE) || check(TokenType.SUSPEND) || check(TokenType.EXPECT) ||
            check(TokenType.ACTUAL) || check(TokenType.OVERRIDE) || check(TokenType.OPEN) ||
            check(TokenType.ABSTRACT) || check(TokenType.FINAL) ||
            check(TokenType.PRIVATE) || check(TokenType.PROTECTED) || check(TokenType.PUBLIC) ||
            check(TokenType.INTERNAL)
        ) {
            if (check(TokenType.DATA) || check(TokenType.SEALED)) break
            modifiers += advance().lexeme
        }

        return when {
            match(TokenType.FUN) || (modifiers.contains("suspend") && match(TokenType.FUN)) ->
                parseFunction(annotations, modifiers)
            match(TokenType.VAL) -> parseProperty(false, modifiers)
            match(TokenType.VAR) -> parseProperty(true, modifiers)
            match(TokenType.CLASS) -> parseClassLike("class", annotations, modifiers)
            match(TokenType.OBJECT) -> parseClassLike("object", annotations, modifiers)
            match(TokenType.INTERFACE) -> parseClassLike("interface", annotations, modifiers)
            match(TokenType.ENUM) -> {
                match(TokenType.CLASS)
                parseClassLike("enum", annotations, modifiers)
            }
            match(TokenType.DATA) -> when {
                match(TokenType.CLASS) -> parseClassLike("data class", annotations, modifiers)
                match(TokenType.OBJECT) -> parseClassLike("data object", annotations, modifiers)
                else -> null
            }
            match(TokenType.SEALED) -> when {
                match(TokenType.CLASS) -> parseClassLike("sealed class", annotations, modifiers)
                match(TokenType.INTERFACE) -> parseClassLike("sealed interface", annotations, modifiers)
                else -> null
            }
            check(TokenType.IDENTIFIER) && peek().lexeme == "typealias" -> {
                advance()
                val name = consumeIdentifier()
                match(TokenType.ASSIGN)
                Decl.TypeAlias(name, parseTypeName())
            }
            else -> null
        }
    }

    private fun parseAnnotation(): String {
        match(TokenType.AT)
        val name = if (check(TokenType.IDENTIFIER) || check(TokenType.COMPOSABLE) || check(TokenType.PAGE))
            advance().lexeme else "<unknown>"
        if (match(TokenType.LPAREN)) skipBalanced(TokenType.LPAREN, TokenType.RPAREN)
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
            match(TokenType.LBRACE) -> Body.Block(parseStatementsUntil(TokenType.RBRACE))
            match(TokenType.ASSIGN) -> Body.Expression(parseExpression())
            else -> null
        }
        return Decl.Function(name, params, returnType, body, annotations, modifiers, range(start, pos()))
    }

    private fun parseProperty(mutable: Boolean, modifiers: List<String>): Decl.Property {
        val start = previous().pos
        val name = consumeIdentifier()
        var type: String? = null
        if (match(TokenType.COLON)) type = parseTypeName()
        val init = if (match(TokenType.ASSIGN)) parseExpression() else null
        return Decl.Property(mutable, name, type, init, modifiers, range(start, pos()))
    }

    private fun parseClassLike(kind: String, annotations: List<String>, modifiers: List<String>): Decl.ClassLike {
        val start = previous().pos
        val name = consumeIdentifier()
        if (match(TokenType.LT)) skipBalanced(TokenType.LT, TokenType.GT)
        if (match(TokenType.LPAREN)) skipBalanced(TokenType.LPAREN, TokenType.RPAREN)
        if (match(TokenType.COLON)) {
            while (!isAtEnd() && !check(TokenType.LBRACE) && !isDeclarationStart()) advance()
        }
        val members = mutableListOf<Decl>()
        if (match(TokenType.LBRACE)) {
            while (!isAtEnd() && !check(TokenType.RBRACE)) {
                parseDeclaration()?.let { members += it } ?: advance()
            }
            expect(TokenType.RBRACE, "}")
        }
        return Decl.ClassLike(kind, name, members, annotations, modifiers, range(start, pos()))
    }

    private fun parseStatementsUntil(end: TokenType): List<Stmt> {
        val stmts = mutableListOf<Stmt>()
        while (!isAtEnd() && !check(end)) {
            stmts += parseStatement()
        }
        match(end)
        return stmts
    }

    private fun parseStatement(): Stmt {
        when {
            match(TokenType.RETURN) -> {
                val expr = if (!check(TokenType.RBRACE) && !check(TokenType.SEMICOLON) && !isAtEnd())
                    parseExpression() else null
                match(TokenType.SEMICOLON)
                return Stmt.Return(expr)
            }
            match(TokenType.VAL) -> return parseLocal(false)
            match(TokenType.VAR) -> return parseLocal(true)
            else -> {
                if (isDeclarationStart() || check(TokenType.RBRACE) || check(TokenType.EOF)) return Stmt.Empty
                val expr = parseExpression()
                match(TokenType.SEMICOLON)
                return Stmt.ExprStmt(expr)
            }
        }
    }

    private fun parseLocal(mutable: Boolean): Stmt {
        val name = consumeIdentifier()
        var type: String? = null
        if (match(TokenType.COLON)) type = parseTypeName()
        val init = if (match(TokenType.ASSIGN)) parseExpression() else null
        match(TokenType.SEMICOLON)
        return Stmt.LocalVar(mutable, name, type, init)
    }

    // Pratt-style-ish expression parsing (precedence climbing via nested functions)
    private fun parseExpression(): Expr = parseAssignment()

    private fun parseAssignment(): Expr {
        val expr = parseOr()
        if (match(TokenType.ASSIGN)) {
            val value = parseAssignment()
            return Expr.Assign(expr, value)
        }
        return expr
    }

    private fun parseOr(): Expr {
        var expr = parseAnd()
        while (match(TokenType.OROR)) {
            val right = parseAnd()
            expr = Expr.Binary(expr, "||", right)
        }
        return expr
    }

    private fun parseAnd(): Expr {
        var expr = parseEquality()
        while (match(TokenType.ANDAND)) {
            val right = parseEquality()
            expr = Expr.Binary(expr, "&&", right)
        }
        return expr
    }

    private fun parseEquality(): Expr {
        var expr = parseComparison()
        while (true) {
            val op = when {
                match(TokenType.EQEQ) -> "=="
                match(TokenType.NEQ) -> "!="
                else -> break
            }
            expr = Expr.Binary(expr, op, parseComparison())
        }
        return expr
    }

    private fun parseComparison(): Expr {
        var expr = parseRange()
        while (true) {
            val op = when {
                match(TokenType.LT) -> "<"
                match(TokenType.GT) -> ">"
                match(TokenType.LTE) -> "<="
                match(TokenType.GTE) -> ">="
                else -> break
            }
            expr = Expr.Binary(expr, op, parseRange())
        }
        return expr
    }

    private fun parseRange(): Expr {
        var expr = parseAdd()
        if (match(TokenType.RANGE)) expr = Expr.Binary(expr, "..", parseAdd())
        return expr
    }

    private fun parseAdd(): Expr {
        var expr = parseMul()
        while (true) {
            val op = when {
                match(TokenType.PLUS) -> "+"
                match(TokenType.MINUS) -> "-"
                else -> break
            }
            expr = Expr.Binary(expr, op, parseMul())
        }
        return expr
    }

    private fun parseMul(): Expr {
        var expr = parseUnary()
        while (true) {
            val op = when {
                match(TokenType.STAR) -> "*"
                match(TokenType.SLASH) -> "/"
                match(TokenType.PERCENT) -> "%"
                else -> break
            }
            expr = Expr.Binary(expr, op, parseUnary())
        }
        return expr
    }

    private fun parseUnary(): Expr {
        if (match(TokenType.NOT)) return Expr.Unary("!", parseUnary())
        if (match(TokenType.MINUS)) return Expr.Unary("-", parseUnary())
        if (match(TokenType.PLUS)) return Expr.Unary("+", parseUnary())
        return parseCall()
    }

    private fun parseCall(): Expr {
        var expr = parsePrimary()
        while (true) {
            when {
                match(TokenType.LPAREN) -> {
                    val args = mutableListOf<Expr>()
                    if (!check(TokenType.RPAREN)) {
                        do args += parseExpression() while (match(TokenType.COMMA))
                    }
                    expect(TokenType.RPAREN, ")")
                    expr = Expr.Call(expr, args)
                }
                match(TokenType.DOT) -> {
                    val name = consumeIdentifier()
                    expr = Expr.Member(expr, name)
                }
                match(TokenType.LBRACKET) -> {
                    val index = parseExpression()
                    expect(TokenType.RBRACKET, "]")
                    expr = Expr.Index(expr, index)
                }
                else -> break
            }
        }
        return expr
    }

    private fun parsePrimary(): Expr {
        return when {
            match(TokenType.INT) -> Expr.Literal(previous().lexeme, LitKind.INT)
            match(TokenType.FLOAT) -> Expr.Literal(previous().lexeme, LitKind.FLOAT)
            match(TokenType.STRING) -> Expr.Literal(previous().lexeme, LitKind.STRING)
            match(TokenType.CHAR) -> Expr.Literal(previous().lexeme, LitKind.CHAR)
            match(TokenType.TRUE) -> Expr.Literal("true", LitKind.BOOL)
            match(TokenType.FALSE) -> Expr.Literal("false", LitKind.BOOL)
            match(TokenType.NULL) -> Expr.Literal("null", LitKind.NULL)
            match(TokenType.IDENTIFIER) || checkSoftIdent() -> Expr.Ident(advance().lexeme)
            match(TokenType.LPAREN) -> {
                val e = parseExpression()
                expect(TokenType.RPAREN, ")")
                e
            }
            else -> {
                diagnostics.error("Expected expression", peek().pos)
                advance()
                Expr.Unknown
            }
        }
    }

    private fun checkSoftIdent(): Boolean =
        peek().type in setOf(
            TokenType.REGISTRY, TokenType.PACKET, TokenType.ENTITY,
            TokenType.ON, TokenType.ONEACH, TokenType.SCENE3D, TokenType.COMPOSABLE, TokenType.PAGE
        )

    private fun parseTypeName(): String {
        val parts = mutableListOf<String>()
        if (match(TokenType.LPAREN)) {
            parts += "("
            while (!isAtEnd() && !check(TokenType.RPAREN)) parts += advance().lexeme
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
                while (!isAtEnd() && !check(TokenType.GT)) parts += advance().lexeme
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

    private fun skipBalanced(open: TokenType, close: TokenType) {
        var depth = 1
        while (!isAtEnd() && depth > 0) {
            when (advance().type) {
                open -> depth++
                close -> depth--
                else -> {}
            }
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
        TokenType.INLINE, TokenType.SUSPEND, TokenType.EXPECT, TokenType.ACTUAL, TokenType.VALUE
    )

    private fun consumeIdentifier(): String {
        val t = peek()
        if (t.type == TokenType.IDENTIFIER || checkSoftIdent() || t.lexeme.isNotEmpty() && t.type != TokenType.EOF) {
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