package l.compiler

enum class TokenType {
    // Literals
    IDENTIFIER,
    INT,
    FLOAT,
    STRING,
    CHAR,
    TRUE,
    FALSE,
    NULL,

    // Keywords (subset + L soft keywords)
    PACKAGE, IMPORT, CLASS, INTERFACE, OBJECT, FUN, VAL, VAR,
    IF, ELSE, WHEN, FOR, WHILE, DO, RETURN, BREAK, CONTINUE,
    THIS, SUPER, THROW, TRY, CATCH, FINALLY, AS, IS, IN,
    DATA, SEALED, ENUM, ANNOTATION, VALUE, INLINE, SUSPEND,
    EXPECT, ACTUAL, OVERRIDE, OPEN, ABSTRACT, FINAL,
    PRIVATE, PROTECTED, PUBLIC, INTERNAL,
    // L domain soft keywords (recognized as identifiers or soft)
    REGISTRY, PACKET, ENTITY, ON, ONEACH, COMPOSABLE, PAGE, SCENE3D,

    // Operators & punctuation
    PLUS, MINUS, STAR, SLASH, PERCENT,
    EQ, EQEQ, NEQ, LT, GT, LTE, GTE,
    ANDAND, OROR, NOT,
    ARROW, DOUBLE_ARROW,
    DOT, COMMA, COLON, COLONCOLON, SEMICOLON,
    LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,
    QUEST, QUEST_DOT, ELVIS,
    AT, RANGE, DOTDOTDOT,
    ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN,

    // Special
    NEWLINE,
    EOF,
    UNKNOWN
}

data class SourcePos(val line: Int, val column: Int, val offset: Int)

data class Token(
    val type: TokenType,
    val lexeme: String,
    val pos: SourcePos
)