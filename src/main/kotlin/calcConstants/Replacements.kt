package calcConstants

val replacements = mapOf(
    Regex("sqrt\\((.+)\\)") to "root($1, 2)",
    Regex("cbrt\\((.+)\\)") to "root($1, 3)",
    Regex("factorial\\((.+)\\)") to "multifactorial($1, 1)",
    Regex("factorial2\\((.+)\\)") to "multifactorial($1, 2)",
    Regex("\\(?sum x=(.+)->(.+): ?(.+)(?=\\)\\s*)\\)?") to "sum($3, $1, $2)"
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
    }
)