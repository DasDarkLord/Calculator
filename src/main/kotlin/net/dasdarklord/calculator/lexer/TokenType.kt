package net.dasdarklord.calculator.lexer

enum class TokenType {
    // Types
    NUMBER("num"),
    STRING("string"),
    IDENTIFIER("id"),
    UNDEFINED("undefined", word = "undefined"),
    TRUE("true", word = "true"),
    FALSE("false", word = "false"),

    // Functions
    CLASS_FUNCTION_CALL("func_call0"),
    FUNCTION_CALL("func_call"),
    IF("conditional", word = "if"),
    ELSE("else", word = "else"),

    // Operators
    ADDITION("add", '+'),
    SUBTRACTION("sub", '-'),
    MULTIPLICATION("mul", '*'),
    IMPLICIT_MULTIPLICATION("imul"),
    DIVISION("div", '/'),
    EXPONENTIATION("pow", '^'),
    MODULUS("mod", '%'),
    FACTORIAL("factorial", '!'),
    ASSIGN("eq", '='),

    // Checks
    EQUALS("eq0", word = "=="),
    NOT_EQUALS("neq", word = "!="),
    GREATER("greater", '>'),
    GREATER_EQUAL("geq", word = ">="),
    LESS("less", '<'),
    LESS_EQUAL("leq", word = "<="),
    TERNARY("ternary", '?'),

    // Misc
    COMMA("comma", ','),
    COLON("colon", ':'),
    OPEN_CURLY("ocurly", '{'),
    CLOSED_CURLY("ccurly", '}'),
    OPEN_BRACKET("obracket", '['),
    CLOSED_BRACKET("cbracket", ']'),
    OPEN_PARENTHESIS("oparen", '('),
    CLOSED_PARENTHESIS("cparen", ')'),
    COALESCING("coalescing", word = "?:"),
    WHITESPACE("white", ' '),
    ENDISTIC("endistic", word = "endistic"), // Endistic asked to be a token
    UNKNOWN_SYMBOL("unknown")
    ;

    val id: String;
    val symbol: Char?
    val word: String?
    constructor(id: String, symbol: Char? = null, word: String? = null) {
        this.id = id;
        this.symbol = symbol;
        this.word = word
    }
}