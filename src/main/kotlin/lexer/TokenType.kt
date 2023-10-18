package lexer

enum class TokenType {
    NUMBER("num"),
    STRING("string"),
    IDENTIFIER("id"),
    FUNCTION_CALL("func_call"),
    ADDITION("add", '+'),
    SUBTRACTION("sub", '-'),
    MULTIPLICATION("mul", '*'),
    IMPLICIT_MULTIPLICATION("imul"),
    DIVISION("div", '/'),
    EXPONENTIATION("pow", '^'),
    EQUALS("eq", '='),
    FACTORIAL("factorial", '!'),
    MODULUS("mod", '%'),
    COMMA("comma", ','),
    COLON("colon", ':'),
    OPEN_CURLY("ocurly", '{'),
    CLOSED_CURLY("ccurly", '}'),
    OPEN_BRACKET("obracket", '['),
    CLOSED_BRACKET("cbracket", ']'),
    OPEN_PARENTHESIS("oparen", '('),
    CLOSED_PARENTHESIS("cparen", ')');

    val id: String;
    val symbol: Char?;
    constructor(id: String, symbol: Char? = null) {
        this.id = id;
        this.symbol = symbol;
    }
}