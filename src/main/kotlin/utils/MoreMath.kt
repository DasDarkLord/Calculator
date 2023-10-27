package utils

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator
import java.math.BigDecimal
import java.math.RoundingMode
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

fun multifactorial(number: Double, factorial: Double = 1.0, i: Int = 0): Double {
    if (number < 0) return Double.NaN
    if (factorial == 1.0) return number.gammaFactorial()
    if (number <= factorial) return number
    return number * multifactorial(number - factorial, factorial, i + 1)
}

fun Double.gammaFactorial(): Double {
    return gamma(this + 1)
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

fun gamma(x: Double): Double {
    val bigDecimal = BigDecimal(gammaLanczos(x)).setScale(8, RoundingMode.HALF_UP)
    return bigDecimal.toDouble()
}

fun integrate(a: Double, b: Double, f: (Double)->Double): Double {
    val value = integral(
        a,
        b,
        10000,
        ::simpson,
        f
    )
    val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
    return bigDecimal.toDouble()
}