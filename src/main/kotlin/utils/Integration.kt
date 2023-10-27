package utils

// Credits to: https://gist.github.com/peheje/50568e85951dbd13fd0c092ac2c076aa

typealias Function = (x: Double) -> Double
typealias Rule = (f: Function, x: Double, h: Double) -> Double

fun leftRectangle(f: Function, x: Double, h: Double) = f(x)
fun midRectangle(f: Function, x: Double, h: Double) = f(x + h / 2.0)
fun rightRectangle(f: Function, x: Double, h: Double) = f(x + h)
fun trapezium(f: Function, x: Double, h: Double) = (f(x) + f(x + h)) / 2.0
fun simpson(f: Function, x: Double, h: Double) = (f(x) + 4.0 * f(x + h / 2.0) + f(x + h)) / 6.0

fun integral(
    from: Double,
    to: Double,
    columns: Int,
    rule: Rule,
    f: (Double) -> Double
): Double {
    val h = (to - from) / columns
    var sum = 0.0
    for (i in 0 until columns) {
        val x = from + i * h
        sum += rule(f, x, h)
    }

    val integral = sum * h
    // println(integral) Calc
    return integral
}