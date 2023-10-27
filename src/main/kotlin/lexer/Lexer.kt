package lexer

import calcConstants.advancedReplacements
import calcConstants.constants
import calcConstants.replacements
import calcConstants.userConstants
import calcFunctions.functions
import calcFunctions.userFunctions
import utils.deepCopy

class Lexer(val source: String) {
    var replacementsEnabled = true

    fun replacements(r: Boolean) {
        replacementsEnabled = r
    }

    fun lexTokens(): MutableList<Token> {
        val tokens = mutableListOf<Token>()
        var src = source
        if (replacementsEnabled) {
            for ((pattern, replaceWith) in replacements) {
                while (pattern.find(src) != null) src = pattern.replaceFirst(src, replaceWith.trim())
            }

            for ((pattern, replaceWith) in advancedReplacements) src = pattern.replace(src, replaceWith)
        }
        val source = src

        var position = 0
        while (position < source.length) {
            when {
                source[position].isDigit() -> {
                    var num = ""
                    var type = "norm" // norm, bin, hex
                    var amountPoint = 0

                    var isHexLet = false

                    while (position < source.length && (source[position].isDigit() || (source[position] == '.' && amountPoint == 0) || source[position] == '_' || source[position] == 'b' || source[position] == 'x' || isHexLet)) {
                        if (source[position] == 'x' || (source[position] == 'b' && type != "hex")) {
                            if (type != "norm") break
                            if (num != "0") break

                            type = if (source[position] == 'x') "hex" else "bin"
                            num += source[position]

                            position++

                            isHexLet = false
                            if (position < source.length && type == "hex" && source[position].toString().matches(Regex("[abcdefABCDEF]"))) {
                                isHexLet = true
                            }

                            continue
                        }
                        if (source[position] == '_') {
                            position++
                            continue
                        }
                        if (source[position] == '.') amountPoint = 1
                        num += source[position]
                        position++

                        isHexLet = false
                        if (position < source.length && type == "hex" && source[position].toString().matches(Regex("[abcdefABCDEF]"))) {
                            isHexLet = true
                        }
                    }
                    position--

                    var value: Any = num
                    if (type == "norm") {
                        value = if (num.toDoubleOrNull() == null) 0.0 else num.toDouble()
                    }

                    tokens.add(Token(
                        TokenType.NUMBER,
                        value
                    ))
                }
                source[position] == '"' || source[position] == '\'' -> {
                    val startChar = source[position]
                    position++

                    var str = ""
                    var prev = '\u0000'
                    while (position < source.length) {
                        if (source[position] == startChar && prev != '\\') break
                        if (prev == '\\') {
                            when (source[position]) {
                                'n' -> str += "\n"
                                '\\' -> str += "\\"
                                't' -> str += "\t"
                                'r' -> str += "\r"
                                '"' -> str += "\""
                                '\'' -> str += "'"
                            }
                            position++
                            prev = '\u0000'
                            continue
                        }
                        prev = source[position]
                        if (source[position] == '\\') {
                            position++
                            continue
                        }
                        str += source[position]
                        position++
                    }

                    tokens.add(Token(
                        TokenType.STRING,
                        str,
                    ))
                }
                source[position].isLetter() || source[position] == '`' -> {
                    val startWithBacktick = source[position] == '`'
                    if (startWithBacktick) position++

                    var str = ""
                    var amountDigit = 0
                    while (position < source.length) {
                        if (startWithBacktick) {
                            if (source[position] == '`') {
                                position++
                                break
                            }
                        }
                        else if (!(source[position].isLetter() || source[position].isDigit() || source[position] == '_')) break
                        if (amountDigit > 0 && !source[position].isDigit()) break

                        str += source[position]
                        if (!startWithBacktick) if (source[position].isDigit()) amountDigit++
                        position++
                    }

                    if (!startWithBacktick) {
                        val strNoNumbers = str.substring(0, str.length - amountDigit)
                        var found = false
                        for (type in TokenType.entries) {
                            if (type.word != null && type.word == strNoNumbers) {
                                tokens.add(Token(
                                    type,
                                    type.word
                                ))
                                if (amountDigit > 0) position -= amountDigit
                                found = true
                                break
                            }
                        }
                        if (found) continue
                    }

                    val combFuncs = deepCopy(userFunctions)
                    for (function in functions) combFuncs[function.key] = function.value

                    var funcExists = false
                    for (function in combFuncs) for (name in function.key) if (name == str) funcExists = true

                    val combConsts = deepCopy(userConstants)
                    for (constant in constants) combConsts[constant.key] = constant.value

                    var constantExists = false
                    for (constant in combConsts) for (name in constant.key) if (name == str.split(".")[0]) constantExists = true

                    var type = TokenType.IDENTIFIER
                    if (position < source.length && source[position] == '(' && (funcExists || !constantExists)) {
                        type = TokenType.FUNCTION_CALL
                    } else {
                        position -= amountDigit
                        str = str.substring(0, str.length - amountDigit)
                    }
                    position--

                    tokens.add(Token(
                        type,
                        str
                    ))
                }
                source[position] == '.' -> {
                    position++

                    val inBackticks = source[position] == '`'

                    var str = ""

                    while (position < source.length) {
                        if (inBackticks) {
                            if (source[position] == '`') {
                                position++
                                break
                            }
                        } else {
                            if (!(source[position].isLetter() || source[position].isDigit() || source[position] == '_')) break
                        }

                        str += source[position]

                        position++
                    }
                    position--

                    tokens.add(Token(
                        TokenType.CLASS_FUNCTION_CALL,
                        str
                    ))
                }
                else -> {
                    var str = ""
                    while (position < source.length && !source[position].isLetterOrDigit() && source[position] != '"' && source[position] != '\'' && source[position] != '.') {
                        str += source[position]
                        position++
                    }
                    position--

                    val typeLengthComparator = Comparator { a: TokenType, b: TokenType ->
                        val aWord = a.word ?: a.symbol.toString()
                        val bWord = b.word ?: b.symbol.toString()

                        bWord.length - aWord.length
                    }

                    val sortedByLengthEntries = TokenType.entries
                        .filter { it.symbol != null || it.word != null }
                        .sortedWith(typeLengthComparator)

                    for (i in 0..sortedByLengthEntries.size) {
                        for (type in sortedByLengthEntries) {
                            if (str.isEmpty()) break
                            if ((type.word != null && str.contains(type.word)) || (type.symbol != null && str.contains(type.symbol.toString()))) {
                                val op = if (type.word != null && str.contains(type.word)) type.word
                                else type.symbol.toString()
                                if (!str.startsWith(op)) continue

                                tokens.add(Token(
                                    type,
                                    op
                                ))

                                val range = IntRange(str.indexOf(op), str.indexOf(op) + op.length - 1)
                                str = str.removeRange(range)
                                break
                            }
                        }
                    }

                    if (str.isNotEmpty()) {
                        tokens.add(Token(
                            TokenType.UNKNOWN_SYMBOL,
                            str
                        ))
                    }
                }
            }
            position++
        }
        return tokens
    }

}