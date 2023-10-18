import com.google.gson.JsonElement
import com.google.gson.JsonObject
import evaluator.Evaluator
import lexer.Lexer
import org.intellij.lang.annotations.RegExp
import parser.NodeParser

fun main(args: Array<String>) {
    while (true) {
        val user = readln()

        // Lex Input
        val tokens = Lexer(user).lexTokens()

        // Parse to a Tree
        val tree = NodeParser.parseTokens(tokens)
//        println(tree.json())

        // Evaluate tree
        val result = Evaluator.evaluateTree(tree)
        println(prettierVersion(result))
    }
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