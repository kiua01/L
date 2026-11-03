package l.compiler

class Lexer(private val source: String) {
    private var index = 0
    private var line = 1
    private var column = 1

    private val keywords = mapOf(
        // Full English forms
        "package" to TokenType.PACKAGE,
        "import" to TokenType.IMPORT,
        "class" to TokenType.CLASS,
        "interface" to TokenType.INTERFACE,
        "object" to TokenType.OBJECT,
        "fun" to TokenType.FUN,
        "function" to TokenType.FUN,
        "val" to TokenType.VAL,
        "var" to TokenType.VAR,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "when" to TokenType.WHEN,
        "for" to TokenType.FOR,
        "while" to TokenType.WHILE,
        "do" to TokenType.DO,
        "return" to TokenType.RETURN,
        "break" to TokenType.BREAK,
        "continue" to TokenType.CONTINUE,
        "this" to TokenType.THIS,
        "super" to TokenType.SUPER,
        "throw" to TokenType.THROW,
        "try" to TokenType.TRY,
        "catch" to TokenType.CATCH,
        "finally" to TokenType.FINALLY,
        "as" to TokenType.AS,
        "is" to TokenType.IS,
        "in" to TokenType.IN,
        "data" to TokenType.DATA,
        "sealed" to TokenType.SEALED,
        "enum" to TokenType.ENUM,
        "annotation" to TokenType.ANNOTATION,
        "value" to TokenType.VALUE,
        "inline" to TokenType.INLINE,
        "suspend" to TokenType.SUSPEND,
        "expect" to TokenType.EXPECT,
        "actual" to TokenType.ACTUAL,
        "override" to TokenType.OVERRIDE,
        "open" to TokenType.OPEN,
        "abstract" to TokenType.ABSTRACT,
        "final" to TokenType.FINAL,
        "private" to TokenType.PRIVATE,
        "protected" to TokenType.PROTECTED,
        "public" to TokenType.PUBLIC,
        "internal" to TokenType.INTERNAL,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "null" to TokenType.NULL,

        // Modern abbreviated English (same tokens)
        "pkg" to TokenType.PACKAGE,
        "imp" to TokenType.IMPORT,
        "cls" to TokenType.CLASS,
        "iface" to TokenType.INTERFACE,
        "obj" to TokenType.OBJECT,
        "fn" to TokenType.FUN,
        "def" to TokenType.FUN,
        "let" to TokenType.VAL,
        "mut" to TokenType.VAR,
        "ret" to TokenType.RETURN,
        "loop" to TokenType.WHILE,
        "match" to TokenType.WHEN,
        "pub" to TokenType.PUBLIC,
        "priv" to TokenType.PRIVATE,
        "prot" to TokenType.PROTECTED,
        "abs" to TokenType.ABSTRACT,
        "ovrd" to TokenType.OVERRIDE,
        "sus" to TokenType.SUSPEND,

        // Domain soft keywords
        "registry" to TokenType.REGISTRY,
        "reg" to TokenType.REGISTRY,
        "packet" to TokenType.PACKET,
        "pkt" to TokenType.PACKET,
        "entity" to TokenType.ENTITY,
        "ent" to TokenType.ENTITY,
        "on" to TokenType.ON,
        "onEach" to TokenType.ONEACH,
        "Scene3D" to TokenType.SCENE3D
    )

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (!isAtEnd()) {
            skipWhitespaceAndComments()
            if (isAtEnd()) break
            tokens += nextToken()
        }
        tokens += Token(TokenType.EOF, "", pos())
        return tokens
    }

    private fun nextToken(): Token {
        val start = pos()
        val c = advance()

        return when (c) {
            '(' -> Token(TokenType.LPAREN, "(", start)
            ')' -> Token(TokenType.RPAREN, ")", start)
            '{' -> Token(TokenType.LBRACE, "{", start)
            '}' -> Token(TokenType.RBRACE, "}", start)
            '[' -> Token(TokenType.LBRACKET, "[", start)
            ']' -> Token(TokenType.RBRACKET, "]", start)
            ',' -> Token(TokenType.COMMA, ",", start)
            ';' -> Token(TokenType.SEMICOLON, ";", start)
            '@' -> Token(TokenType.AT, "@", start)
            '%' -> Token(TokenType.PERCENT, "%", start)
            '+' -> if (match('=')) Token(TokenType.PLUS_ASSIGN, "+=", start) else Token(TokenType.PLUS, "+", start)
            '-' -> when {
                match('>') -> Token(TokenType.ARROW, "->", start)
                match('=') -> Token(TokenType.MINUS_ASSIGN, "-=", start)
                else -> Token(TokenType.MINUS, "-", start)
            }
            '*' -> Token(TokenType.STAR, "*", start)
            '/' -> Token(TokenType.SLASH, "/", start)
            '=' -> when {
                match('=') -> Token(TokenType.EQEQ, "==", start)
                match('>') -> Token(TokenType.DOUBLE_ARROW, "=>", start)
                else -> Token(TokenType.ASSIGN, "=", start)
            }
            '!' -> if (match('=')) Token(TokenType.NEQ, "!=", start) else Token(TokenType.NOT, "!", start)
            '<' -> if (match('=')) Token(TokenType.LTE, "<=", start) else Token(TokenType.LT, "<", start)
            '>' -> if (match('=')) Token(TokenType.GTE, ">=", start) else Token(TokenType.GT, ">", start)
            '&' -> if (match('&')) Token(TokenType.ANDAND, "&&", start) else Token(TokenType.UNKNOWN, "&", start)
            '|' -> if (match('|')) Token(TokenType.OROR, "||", start) else Token(TokenType.UNKNOWN, "|", start)
            '?' -> when {
                match('.') -> Token(TokenType.QUEST_DOT, "?.", start)
                match(':') -> Token(TokenType.ELVIS, "?:", start)
                else -> Token(TokenType.QUEST, "?", start)
            }
            ':' -> if (match(':')) Token(TokenType.COLONCOLON, "::", start) else Token(TokenType.COLON, ":", start)
            '.' -> when {
                match('.') -> if (match('.')) Token(TokenType.DOTDOTDOT, "...", start) else Token(TokenType.RANGE, "..", start)
                else -> Token(TokenType.DOT, ".", start)
            }
            '"' -> stringToken(start)
            '\'' -> charToken(start)
            else -> when {
                c.isDigit() -> numberToken(c, start)
                c.isLetter() || c == '_' -> identifierToken(c, start)
                else -> Token(TokenType.UNKNOWN, c.toString(), start)
            }
        }
    }

    private fun stringToken(start: SourcePos): Token {
        val sb = StringBuilder()
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\\') {
                advance()
                if (!isAtEnd()) sb.append(advance())
            } else {
                if (peek() == '\n') {
                    line++
                    column = 0
                }
                sb.append(advance())
            }
        }
        if (!isAtEnd()) advance() // closing "
        return Token(TokenType.STRING, sb.toString(), start)
    }

    private fun charToken(start: SourcePos): Token {
        val sb = StringBuilder()
        while (!isAtEnd() && peek() != '\'') {
            if (peek() == '\\') {
                advance()
                if (!isAtEnd()) sb.append(advance())
            } else {
                sb.append(advance())
            }
        }
        if (!isAtEnd()) advance()
        return Token(TokenType.CHAR, sb.toString(), start)
    }

    private fun numberToken(first: Char, start: SourcePos): Token {
        val sb = StringBuilder().append(first)
        while (!isAtEnd() && peek().isDigit()) sb.append(advance())
        if (!isAtEnd() && peek() == '.' && peekNext().isDigit()) {
            sb.append(advance())
            while (!isAtEnd() && peek().isDigit()) sb.append(advance())
            return Token(TokenType.FLOAT, sb.toString(), start)
        }
        return Token(TokenType.INT, sb.toString(), start)
    }

    private fun identifierToken(first: Char, start: SourcePos): Token {
        val sb = StringBuilder().append(first)
        while (!isAtEnd() && (peek().isLetterOrDigit() || peek() == '_')) {
            sb.append(advance())
        }
        val text = sb.toString()
        val type = keywords[text] ?: TokenType.IDENTIFIER
        return Token(type, text, start)
    }

    private fun skipWhitespaceAndComments() {
        while (!isAtEnd()) {
            when (peek()) {
                ' ', '\t', '\r' -> advance()
                '\n' -> {
                    advance()
                    // keep newlines available if needed later; currently skip
                }
                '/' -> when (peekNext()) {
                    '/' -> {
                        while (!isAtEnd() && peek() != '\n') advance()
                    }
                    '*' -> {
                        advance()
                        advance()
                        while (!isAtEnd()) {
                            if (peek() == '*' && peekNext() == '/') {
                                advance()
                                advance()
                                break
                            }
                            if (peek() == '\n') {
                                line++
                                column = 0
                            }
                            advance()
                        }
                    }
                    else -> return
                }
                else -> return
            }
        }
    }

    private fun isAtEnd() = index >= source.length
    private fun peek() = if (isAtEnd()) '\u0000' else source[index]
    private fun peekNext() = if (index + 1 >= source.length) '\u0000' else source[index + 1]
    private fun advance(): Char {
        val c = source[index++]
        if (c == '\n') {
            line++
            column = 1
        } else {
            column++
        }
        return c
    }
    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[index] != expected) return false
        advance()
        return true
    }
    private fun pos() = SourcePos(line, column, index)
}