package utils

fun isAnyNumber(v: Any): Boolean {
    return v is Number
}

fun numToDouble(v: Any): Double {
    if (!isAnyNumber(v)) return 0.0
    v as Number
    return v.toDouble()
}

