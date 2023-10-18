package utils

import kotlin.math.*

fun cot(x: Double): Double {
    return 1.0 / tan(x)
}

fun coth(x: Double): Double {
    return 1.0 / tanh(x)
}

fun sec(x: Double): Double {
    return 1.0 / cos(x)
}

fun csc(x: Double): Double {
    return 1.0 / sin(x)
}

fun sech(x: Double): Double {
    return 1.0 / cosh(x)
}

fun csch(x: Double): Double {
    return 1.0 / sinh(x)
}

fun multifactorial(x: Double, f: Double): Double {
    val rF = f.toInt()
    if (x < 0 || x == 1.0) return x

    return x * multifactorial(x - f, f)
}