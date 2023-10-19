import com.google.gson.JsonElement
import com.google.gson.JsonObject
import evaluator.Evaluator
import lexer.Lexer
import lexer.Token
import lexer.TokenType
import org.intellij.lang.annotations.RegExp
import parser.NodeParser
import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    while (true) {
        print("> ")
        val user = scanner.nextLine()
        print("\u001b[2K\r")

        // Lex Input
        val tokens = Lexer(user).lexTokens()

        print(">> ")
        printTokens(tokens) // print colored version of input

        // Parse to a Tree
        val tree = NodeParser.parseTokens(tokens)
//        println(tree.json())

        // Evaluate tree
        val result = Evaluator.evaluateTree(tree)
        println(prettierVersion(result))
    }
}

fun printTokens(tokens: List<Token>) {
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
            TokenType.CLASS_FUNCTION_CALL -> "\u001b[38;5;141m" + prettierVersion(token.value)
            TokenType.FUNCTION_CALL -> "\u001b[38;5;141m" + prettierVersion(token.value)
            TokenType.IMPLICIT_MULTIPLICATION -> "\u001b[37m*"
            TokenType.OPEN_CURLY -> "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.CLOSED_CURLY ->  "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.OPEN_BRACKET ->  "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.CLOSED_BRACKET ->  "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.OPEN_PARENTHESIS ->  "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
            TokenType.CLOSED_PARENTHESIS -> "\u001b[38;5;${depthColor}m" + prettierVersion(token.value)
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
    return str.replace("\n", "\\n").replace("\t", "\\t")
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