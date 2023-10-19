package lexer

import calcConstants.constants
import calcConstants.replacements
import calcConstants.userConstants
import calcFunctions.classFunctions
import calcFunctions.functions
import calcFunctions.userFunctions
import utils.deepCopy

class Lexer(val source: String) {

    fun lexTokens(): MutableList<Token> {
        val tokens = mutableListOf<Token>()
        var source = source.trim()
        for (replacement in replacements) source = source.replace(replacement.key, replacement.value)

        var position = 0
        while (position < source.length) {
            when {
                source[position].isDigit() -> {
                    var num = ""
                    var amountPoint = 0
                    while (position < source.length && (source[position].isDigit() || (source[position] == '.' && amountPoint == 0))) {
                        if (source[position] == '.') amountPoint = 1
                        num += source[position]
                        position++
                    }
                    position--

                    tokens.add(Token(
                        TokenType.NUMBER,
                        if (num.toDoubleOrNull() == null) 0 else num.toDouble()
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
                    val startWithThing = source[position] == '`'
                    if (startWithThing) position++

                    var str = ""
                    var amountDigit = 0
                    while (position < source.length) {
                        if (startWithThing) {
                            if (source[position] == '`') {
                                position++
                                break
                            }
                        }
                        else if (!(source[position].isLetter() || source[position].isDigit() || source[position] == '_' || source[position] == '.')) break

                        str += source[position]
                        if (!startWithThing) if (source[position].isDigit()) amountDigit++
                        position++
                    }

                    val combFuncs = deepCopy(userFunctions)
                    for (function in functions) combFuncs[function.key] = function.value

                    var funcExists = false
                    for (function in combFuncs) for (name in function.key) if (name == str) funcExists = true

                    var type = TokenType.IDENTIFIER
                    if (position < source.length && source[position] == '(' && funcExists) {
                        type = TokenType.FUNCTION_CALL
                    } else {
                        position -= amountDigit
                        str = str.substring(0, str.length - amountDigit)
                    }
                    position--

                    if (str.contains(".")) {
                        val id = str.split(".")[0]
                        val func = str.split(".")[1]

                        funcExists = false
                        for (function in classFunctions) for (name in function.key) if (name == func) funcExists = true

                        tokens.add(Token(
                            type,
                            id
                        ))
                        if (funcExists) {
                            tokens.add(Token(
                                TokenType.CLASS_FUNCTION_CALL,
                                func
                            ))
                        }
                    } else {
                        tokens.add(Token(
                            type,
                            str
                        ))
                    }
                }
                else -> {
                    for (type in TokenType.entries) {
                        if (type.symbol != null && source[position] == type.symbol) {
                            tokens.add(Token(
                                type,
                                type.symbol.toString()
                            ))
                        }
                    }
                }
            }
            position++
        }
        return tokens
    }

}