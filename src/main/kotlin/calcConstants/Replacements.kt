package calcConstants

import kotlin.math.exp

val replacements = mapOf<Regex, String>(
)

val advancedReplacements = mapOf<Regex, (MatchResult) -> CharSequence>(
    Regex("([⁰¹²³⁴⁵⁶⁷⁸⁹]+)") to { result ->
        val superScriptNormalMap = mapOf(
            '⁰' to '0',
            '¹' to '1',
            '²' to '2',
            '³' to '3',
            '⁴' to '4',
            '⁵' to '5',
            '⁶' to '6',
            '⁷' to '7',
            '⁸' to '8',
            '⁹' to '9'
        )

        result.groups[0]!!.value.map {
            return@map if (superScriptNormalMap.containsKey(it)) superScriptNormalMap[it] else it
        }.joinToString("", "^")
    },
    Regex("\\(?sum x=(.+)->(.+): ?(.+)(?=\\)\\s*)\\)?") to { result ->
        var expression = result.groups[3]!!.value
        var addParentheses = false
        var openParen = 0
        for (char in expression) {
            if (char == '(' || char == '[' || char == '{') openParen++
            if (char == ')' || char == ']' || char == '}') openParen--
            if (char == ',' && openParen == 0) addParentheses = true
            if (addParentheses) break
        }
        if (addParentheses) expression = "($expression)"

        "sum($expression, ${result.groups[1]!!.value}, ${result.groups[2]!!.value})".trim()
    }
)