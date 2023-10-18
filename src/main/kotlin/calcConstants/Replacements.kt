package calcConstants

val replacements = mapOf(
    Regex("sqrt\\(([\\d.]+)\\)") to "root($1, 2)",
    Regex("cbrt\\(([\\d.]+)\\)") to "root($1, 3)",
    Regex("factorial\\(([\\d.]+)\\)") to "multifactorial($1, 1)",
    Regex("factorial2\\(([\\d.]+)\\)") to "multifactorial($1, 2)"
)