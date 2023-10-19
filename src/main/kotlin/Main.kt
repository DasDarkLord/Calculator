import evaluator.Evaluator
import lexer.Lexer
import lexer.Token
import lexer.TokenType
import parser.NodeParser
import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    while (true) {
        print("> ")
        val user = scanner.nextLine()
        print("\u001b[2K\r")

        val tokens = Lexer(user).lexTokens()
//        for (token in tokens) {
//            if (token.type == TokenType.WHITESPACE) continue
//            println(token)
//        }
        print(">> ")
        printColored(tokens)


        val tree = NodeParser.parseTokens(tokens)
//        println(tree.json())


        val result = Evaluator.evaluateTree(tree)
        println(prettierVersion(result))
    }
}

fun printColored(tokens: List<Token>) {
    val depths = listOf(
        "134", "104", "144"
    )
    var depth = 0

    var output = ""
    for (token in tokens) {
        if (token.type == TokenType.CLOSED_PARENTHESIS) depth--
        if (token.type == TokenType.CLOSED_BRACKET) depth--
        if (token.type == TokenType.CLOSED_CURLY) depth--
        if (depth < 0) depth = depths.size - 1

        val depthColor = depths[depth]

        output += when (token.type) {
            TokenType.NUMBER -> "\u001b[38;5;1m" + prettierVersion(token.value)
            TokenType.STRING -> "\u001b[38;5;87m\"" + fixEscapes(prettierVersion(token.value)) + "\""
            TokenType.IDENTIFIER -> "\u001b[38;5;221m" + (if (token.value.toString().contains(" ")) "`" else "") + prettierVersion(token.value) + (if (token.value.toString().contains(" ")) "`" else "")
            TokenType.CLASS_FUNCTION_CALL -> "\u001B[38;5;231m.\u001b[38;5;147m" + prettierVersion(token.value)
            TokenType.FUNCTION_CALL -> "\u001b[38;5;147m" + (if (token.value.toString().contains(" ")) "`" else "") + prettierVersion(token.value) + (if (token.value.toString().contains(" ")) "`" else "")
            TokenType.IMPLICIT_MULTIPLICATION, TokenType.UNDEFINED -> "\u001b[37m" + prettierVersion(token.value)
            TokenType.OPEN_CURLY, TokenType.CLOSED_CURLY, TokenType.OPEN_BRACKET, TokenType.CLOSED_BRACKET, TokenType.OPEN_PARENTHESIS, TokenType.CLOSED_PARENTHESIS -> "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.WHITESPACE -> " "
            else -> "\u001b[38;5;231m" + prettierVersion(token.value)
        }

        if (token.type == TokenType.OPEN_PARENTHESIS) depth++
        if (token.type == TokenType.OPEN_BRACKET) depth++
        if (token.type == TokenType.OPEN_CURLY) depth++

        if (depth >= depths.size) depth = 0
    }
    output += "\u001b[0m"

    println(output)
}

fun fixEscapes(str: String): String {
    return str.replace("\n", "\\n").replace("\t", "\\t").replace("\\\\", "\\\\\\\\")
        .replace("\r", "\\r")
}

/**
 * Makes a prettier version of the input
 */
fun prettierVersion(input: Any): String {
    if (input is Number) {
        if (input is Double || input is Float) {
            val d = input.toDouble()
            if ((d - d.toInt()) > 0) return d.toString()
            return d.toInt().toString()
        } else return input.toString()
    }
    if (input is Map<*, *>) {
        var output = "{"
        for ((key, value) in input) {
            output += "$key: ${prettierVersion(if (value == null) "null" else prettierVersion(value))}, "
        }
        return "${output.replace(Regex(", $"), "")}}"
    }
    if (input is List<*>) {
        var output = "["
        for (value in input) {
            output += "${if (value == null) "null" else prettierVersion(value)}, "
        }
        return "${output.replace(Regex(", $"), "")}]"
    }

    return input.toString()
}