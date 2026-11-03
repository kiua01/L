package l.compiler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LexerTest {
    @Test
    fun tokenizesSimpleFunction() {
        val source = """
            fun main() {
                val x = 42
            }
        """.trimIndent()
        val tokens = Lexer(source).tokenize().filter { it.type != TokenType.EOF }
        assertTrue(tokens.any { it.type == TokenType.FUN })
        assertTrue(tokens.any { it.type == TokenType.VAL })
        assertTrue(tokens.any { it.type == TokenType.INT && it.lexeme == "42" })
    }

    @Test
    fun tokenizesStringAndKeywords() {
        val source = """registry<Block> { "cool" { } }"""
        val tokens = Lexer(source).tokenize()
        assertTrue(tokens.any { it.type == TokenType.REGISTRY })
        assertTrue(tokens.any { it.type == TokenType.STRING && it.lexeme == "cool" })
    }
}

class ParserTest {
    @Test
    fun parsesPackageImportsAndFun() {
        val source = """
            package demo.mod
            import l.minecraft.registry.*
            fun hello(name: String): Unit {}
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        assertEquals("demo.mod", ast.packageName)
        assertEquals(1, ast.imports.size)
        assertTrue(ast.declarations.any { it is Decl.Function && it.name == "hello" })
        val fn = ast.declarations.filterIsInstance<Decl.Function>().first()
        assertEquals(listOf("name"), fn.params.map { it.name })
        assertEquals("String", fn.params[0].type)
        assertFalse(diag.hasErrors)
    }

    @Test
    fun parsesClassWithMembers() {
        val source = """
            @Mod("x")
            object Example {
                val BLOCKS = 1
                fun init() {}
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val obj = ast.declarations.filterIsInstance<Decl.ClassLike>().first()
        assertEquals("object", obj.kind)
        assertEquals("Example", obj.name)
        assertTrue(obj.members.any { it is Decl.Property && it.name == "BLOCKS" })
        assertTrue(obj.members.any { it is Decl.Function && it.name == "init" })
    }

    @Test
    fun resolverFindsNestedSymbols() {
        val source = """
            package demo
            object M {
                fun f() {}
            }
        """.trimIndent()
        val diag = DiagnosticBag()
        val ast = Parser(Lexer(source).tokenize(), diag).parseFile()
        val symbols = Resolver(diag).resolve(ast)
        assertTrue(symbols.any { it.name == "M" && it.kind == "object" })
        assertTrue(symbols.any { it.name == "f" && it.owner?.endsWith("M") == true })
    }
}