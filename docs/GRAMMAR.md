# L Formal Grammar Sketch (Draft)

> Status: Early sketch. Based on Kotlin grammar with planned L extensions.  
> This is **not** a complete or final grammar.

L uses a Kotlin-compatible surface syntax. The grammar below is expressed in a simplified EBNF style.

---

## 1. Lexical Grammar (Summary)

```
Identifier
    : (Letter | '_') (Letter | Digit | '_')*

Keyword
    : 'as' | 'as?' | 'break' | 'class' | 'continue' | 'do' | 'else'
    | 'false' | 'for' | 'fun' | 'if' | 'in' | '!in' | 'interface'
    | 'is' | '!is' | 'null' | 'object' | 'package' | 'return'
    | 'super' | 'this' | 'throw' | 'true' | 'try' | 'typealias'
    | 'typeof' | 'val' | 'var' | 'when' | 'while' | 'by' | 'catch'
    | 'constructor' | 'delegate' | 'dynamic' | 'field' | 'file'
    | 'finally' | 'get' | 'import' | 'init' | 'param' | 'property'
    | 'receiver' | 'set' | 'setparam' | 'where' | 'actual' | 'abstract'
    | 'annotation' | 'companion' | 'const' | 'crossinline' | 'data'
    | 'enum' | 'expect' | 'external' | 'final' | 'infix' | 'inline'
    | 'inner' | 'internal' | 'lateinit' | 'noinline' | 'open'
    | 'operator' | 'out' | 'override' | 'private' | 'protected'
    | 'public' | 'reified' | 'sealed' | 'suspend' | 'tailrec'
    | 'value' | 'vararg'
    // L-specific soft keywords (planned)
    | 'registry' | 'packet' | 'on' | 'onEach' | 'id' | 'mod'

BooleanLiteral : 'true' | 'false'
NullLiteral    : 'null'

IntegerLiteral : Digit+
FloatLiteral   : Digit+ '.' Digit+ (('e' | 'E') ('+' | '-')? Digit+)?

StringLiteral
    : '"' StringContent '"'
    | '"""' RawStringContent '"""'

LineComment  : '//' .* 
BlockComment : '/*' .* '*/'
```

---

## 2. Syntactic Grammar (Core)

### 2.1 File Structure

```
KotlinFile
    : PackageHeader? ImportList TopLevelObject*

PackageHeader
    : 'package' Identifier ('.' Identifier)* ';'? 

ImportList
    : ImportDirective*

ImportDirective
    : 'import' Identifier ('.' Identifier)* ('.' '*')? ( 'as' Identifier )? ';'?

TopLevelObject
    : Declaration
```

### 2.2 Declarations

```
Declaration
    : ClassDeclaration
    | ObjectDeclaration
    | FunctionDeclaration
    | PropertyDeclaration
    | TypeAlias
    | Annotation

ClassDeclaration
    : Modifiers? ('class' | 'interface') Identifier
      TypeParameters?
      PrimaryConstructor?
      (':' DelegationSpecifiers)?
      TypeConstraints?
      ClassBody?

ObjectDeclaration
    : Modifiers? 'object' Identifier
      (':' DelegationSpecifiers)?
      ClassBody?

FunctionDeclaration
    : Modifiers? 'fun' TypeParameters?
      (ReceiverType '.')?
      Identifier
      FunctionValueParameters
      (':' Type)?
      TypeConstraints?
      FunctionBody?

PropertyDeclaration
    : Modifiers? ('val' | 'var')
      TypeParameters?
      (ReceiverType '.')?
      Identifier
      (':' Type)?
      TypeConstraints?
      (('=' Expression) | PropertyDelegate)?
      ((Getter? Setter?) | (Setter? Getter?))
```

### 2.3 Types

```
Type
    : TypeModifiers ParenthesizedType
    | TypeModifiers NullableType
    | TypeModifiers FunctionType
    | TypeModifiers UserType
    | TypeModifiers 'dynamic'

NullableType
    : Type '?' 

FunctionType
    : (ReceiverType '.')? FunctionTypeParameters '->' Type

UserType
    : SimpleUserType ('.' SimpleUserType)*

SimpleUserType
    : Identifier TypeArguments?
```

### 2.4 Statements & Expressions

```
Statement
    : Declaration
    | Assignment
    | LoopStatement
    | Expression

Expression
    : Disjunction  // full precedence climbing matching Kotlin

// Simplified top levels:
Disjunction     : Conjunction ('||' Conjunction)*
Conjunction     : Equality ('&&' Equality)*
Equality        : Comparison (('==' | '!=' | '===' | '!==') Comparison)*
Comparison      : GenericCallLikeComparison (('<' | '>' | '<=' | '>=') GenericCallLikeComparison)*
...             // continues down to AtomicExpression
```

---

## 3. L-Specific Extensions (Planned)

These are **soft-keyword / DSL** constructs that parse as normal Kotlin function/lambda calls in the early stages, later potentially gaining dedicated syntax support.

### 3.1 Registry DSL

```
RegistryExpression
    : 'registry' '<' Type '>' LambdaLiteral
```

Inside the lambda, block-like builders:

```
RegistryEntry
    : StringLiteral LambdaLiteral          // "name" { ... }
    | Identifier LambdaLiteral             // name { ... }
```

### 3.2 Event Handlers

```
EventHandler
    : 'on' '<' Type '>' LambdaLiteral
    | 'onEach' '<' Type '>' LambdaLiteral
```

### 3.3 Packet Definition

```
PacketDeclaration
    : 'packet' '<' Type '>' 
      ( '(' FunctionValueParameters ')' )?
      LambdaLiteral
```

### 3.4 Identifier Helper

```
IdentifierExpression
    : 'id' '(' StringLiteral (',' StringLiteral)? ')'
    | StringLiteral '.id'
```

### 3.5 Mod Annotation / Entry Point

```
ModAnnotation
    : '@Mod' '(' StringLiteral ')'
```

---

## 4. Precedence & Associativity

L follows Kotlin’s operator precedence and associativity rules exactly for all standard operators.

---

## 5. Notes

- Soft keywords (`registry`, `packet`, `on`, etc.) are only treated specially in specific syntactic positions.
- The goal is that any valid Kotlin code is valid L code (with very few exceptions, if any).
- L extensions should degrade gracefully to normal function calls / builders when the compiler does not recognize special context.

---

## Next Steps for Grammar

- [ ] Full expression precedence table
- [ ] Complete statement grammar
- [ ] Annotation use-site targets
- [ ] Context receivers syntax
- [ ] Exact rules for L soft keywords
- [ ] Recovery / error production rules for diagnostics