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
    if (x < 0 || x == 1.0) return x

    return x * multifactorial(x - f, f)
}

fun negativeFactorial(x: Double): Double {
    return gammaLanczos(x)
}

fun gammaLanczos(x: Double): Double {
    var xx = x
    val p = doubleArrayOf(
        0.99999999999980993,
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    )
    val g = 7
    if (xx < 0.5) return Math.PI / (Math.sin(Math.PI * xx) * gammaLanczos(1.0 - xx))
    xx--
    var a = p[0]
    val t = xx + g + 0.5
    for (i in 1 until p.size) a += p[i] / (xx + i)
    return Math.sqrt(2.0 * Math.PI) * Math.pow(t, xx + 0.5) * Math.exp(-t) * a
}