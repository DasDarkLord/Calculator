package calcConstants

val constants = mapOf<List<String>, Any>(
    listOf("pi") to 3.14159,
    listOf("e", "euler") to 2.71828,
    listOf("infinity", "inf", "positive_infinity") to Double.POSITIVE_INFINITY,
    listOf("NaN", "nan", "NAN") to Double.NaN
)

val userConstants = HashMap<List<String>, Any>()