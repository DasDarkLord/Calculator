package calcConstants

val replacements = mapOf(
    Regex("sqrt\\((.+)\\)") to "root($1, 2)",
    Regex("cbrt\\((.+)\\)") to "root($1, 3)",
    Regex("factorial\\((.+)\\)") to "multifactorial($1, 1)",
    Regex("factorial2\\((.+)\\)") to "multifactorial($1, 2)",
    Regex("\\(?sum x=(.+)->(.+): ?(.+)(?=\\)\\s*)\\)?") to "sum($3, $1, $2)"
)