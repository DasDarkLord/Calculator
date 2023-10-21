package calcConstants

import kotlin.math.E
import kotlin.math.PI

val constants = mutableMapOf(
    listOf("pi", "Archimedes' Constant (pi)", "Archimedes' Constant") to PI,
    listOf("e", "euler", "Base of Natural Logarithms", "Base of Natural Logarithms (e)") to E,
    listOf("infinity", "inf", "positive_infinity") to Double.POSITIVE_INFINITY,
    listOf("NaN", "nan", "NAN") to Double.NaN,
    listOf("colors") to mapOf(
        "black" to "\u001B[30m",
        "red" to "\u001B[31m",
        "geren" to "\u001B[32m",
        "yellow" to "\u001B[33m",
        "blue" to "\u001B[34m",
        "meganta" to "\u001B[35m",
        "cyan" to "\u001b[36m",
        "white" to "\u001B[37m",
        "default" to "\u001B[39m",
        "reset" to "\u001b[0m"
    )
)

fun constantExists(name: String): Boolean {
    for (const in constants) {
        for (constName in const.key) if (constName == name) return true
    }

    return false
}

val userConstants = HashMap<List<String>, Any>()