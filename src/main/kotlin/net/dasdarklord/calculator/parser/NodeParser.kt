package net.dasdarklord.calculator.parser

import net.dasdarklord.calculator.evaluator.Evaluator
import net.dasdarklord.calculator.evaluator.Undefined
import net.dasdarklord.calculator.lexer.Token
import net.dasdarklord.calculator.lexer.TokenType
import net.dasdarklord.calculator.utils.deepCopy

class NodeParser(private val tokens: MutableList<Token>) {
    private var index = 0

    private fun parseFunction(): TreeNode {
        val token = tokens[index]
        index++

        val argumentTokens = mutableListOf<MutableList<Token>>()
        val currentTokens = mutableListOf<Token>()

        var openParens = 0
        while (index < tokens.size) {
            if (tokens[index].type == TokenType.OPEN_PARENTHESIS) openParens++
            if (tokens[index].type == TokenType.OPEN_BRACKET) openParens++
            if (tokens[index].type == TokenType.OPEN_CURLY) openParens++
            if (tokens[index].type == TokenType.CLOSED_PARENTHESIS) openParens--
            if (tokens[index].type == TokenType.CLOSED_BRACKET) openParens--
            if (tokens[index].type == TokenType.CLOSED_CURLY) openParens--

            if (openParens == 0) break

            if (tokens[index].type == TokenType.COMMA && openParens == 1) {
                argumentTokens.add(deepCopy(currentTokens))
                currentTokens.clear()
                index++
                continue
            }

            if (tokens[index].type == TokenType.OPEN_PARENTHESIS && openParens == 1) {
                index++
                continue
            }
            currentTokens.add(tokens[index])

            index++
        }

        if (currentTokens.size > 0) argumentTokens.add(currentTokens)

        val treeArguments = mutableListOf<TreeNode>()
        for (exprTokens in argumentTokens) {
            treeArguments.add(parseTokens(exprTokens))
        }

        index++
        return parseOtherStuff(
            TreeNode(
            "func_call",
            value = token.value,
            arguments = treeArguments
        )
        )
    }

    private fun parseScope(): TreeNode {
        val startCurly = tokens[index].type == TokenType.OPEN_CURLY
        if (startCurly) index++

        val scopeExpr = parseExpression()
        if (startCurly && tokens[index].type != TokenType.CLOSED_CURLY) error("Expected closing brackets, got ${tokens[index].type}")
        if (startCurly) index++

        return parseOtherStuff(scopeExpr)
    }

    private fun parseIf(): TreeNode {
        val token = tokens[index]
        if (tokens.size < index + 1) error("Expected opening parenthesis, got nothing")
        val nextToken = tokens[index + 1]
        if (nextToken.type != TokenType.OPEN_PARENTHESIS) error("Expected opening parenthesis, got ${nextToken.type}")
        index += 2

        val parenthesisExpression = parseExpression()
        if (tokens[index].type != TokenType.CLOSED_PARENTHESIS) error("Expected closing parenthesis, got ${tokens[index].type}")
        index++

        if (tokens[index].type == TokenType.IMPLICIT_MULTIPLICATION) index++

        val startCurly = tokens[index].type == TokenType.OPEN_CURLY
        if (startCurly) index++

        val ifExpression = parseScope()

        var elseExpression: TreeNode? = null
        if (index < tokens.size && tokens[index].type == TokenType.ELSE) {
            index++

            elseExpression = parseScope()
        }

        return parseOtherStuff(
            TreeNode(
            token.type.id,
            right = ifExpression,
            left = elseExpression,
            value = parenthesisExpression
        )
        )
    }

    private fun parseOtherStuff(node: TreeNode): TreeNode {
        var isNodeChanged = true
        var currentNode = node

        while (isNodeChanged) {
            val newNode = parseFactorial(parseIndex(parseClassFunction(currentNode)))
            isNodeChanged = currentNode != newNode
            currentNode = newNode.copy()
        }

        return currentNode
    }

    private fun parseIndex(node: TreeNode): TreeNode {
        if (index < tokens.size) {
            if (tokens[index].type == TokenType.OPEN_BRACKET) {
                index++

                val expr = parseExpression()
                if (index < tokens.size && tokens[index].type == TokenType.CLOSED_BRACKET) {
                    index++
                    return TreeNode(
                        "index",
                        left = node,
                        right = expr
                    )
                }
            }
        }

        return node
    }

    private fun parseFactorial(node: TreeNode): TreeNode {
        if (index < tokens.size) {
            var factorial = 0
            while (index < tokens.size && tokens[index].type == TokenType.FACTORIAL) {
                factorial++
                index++
            }

            return if (factorial == 0) node
            else {
                TreeNode(
                    "factorial",
                    left = node,
                    right = TreeNode(
                        "number",
                        value = factorial
                    )
                )
            }
        }

        return node
    }

    private fun parseClassFunction(node: TreeNode): TreeNode {
        if (index < tokens.size) {
            val token = tokens[index]
            if (token.type != TokenType.CLASS_FUNCTION_CALL) return node

            return TreeNode(
                token.type.id,
                left = node,
                right = parseFunction()
            )
        }

        return node
    }

    private fun parseUndefined(): TreeNode {
        val token = tokens[index]
        index++
        return TreeNode("undefined", value = token.value)
    }

    private fun parseTrue(): TreeNode {
        val token = tokens[index]
        index++
        return TreeNode("true", value = token.value)
    }

    private fun parseFalse(): TreeNode {
        val token = tokens[index]
        index++
        return TreeNode("false", value = token.value)
    }

    private fun parseNumber(): TreeNode {
        val token = tokens[index]
        index++

        var numberValue = 0.0
        if (token.value is String) {
            if (token.value.startsWith("0b")) numberValue = (token.value.replace(Regex("^0b"), "0")).toInt(2).toDouble()
            else if (token.value.startsWith("0x")) numberValue = (token.value.replace(Regex("^0x"), "0")).toInt(16).toDouble()
        } else {
            numberValue = token.value as Double
        }

        return parseOtherStuff(TreeNode("number", value = numberValue))
    }

    private fun parseString(): TreeNode {
        val token = tokens[index]
        index++
        return parseOtherStuff(TreeNode("string", value = token.value))
    }

    private fun parseIdentifier(): TreeNode {
        val token = tokens[index]
        index++
        return parseOtherStuff(TreeNode("id", value = token.value))
    }

    private fun parseEndistic(): TreeNode {
        index++
        return TreeNode("undefined", value = Undefined)
    }

    private fun parseDictionary(): TreeNode {
        val map = mutableMapOf<Any, Any>()

        var openParens = 0

        val currentKeyTokens = mutableListOf<Token>()
        val listKeyTokens = mutableListOf<MutableList<Token>>()

        val currentValueTokens = mutableListOf<Token>()
        val listValueTokens = mutableListOf<MutableList<Token>>()

        while (index < tokens.size && tokens[index].type != TokenType.CLOSED_CURLY) {
            if (tokens[index].type == TokenType.OPEN_PARENTHESIS) openParens++
            if (tokens[index].type == TokenType.OPEN_BRACKET) openParens++
            if (tokens[index].type == TokenType.OPEN_CURLY) openParens++
            if (tokens[index].type == TokenType.CLOSED_PARENTHESIS) openParens--
            if (tokens[index].type == TokenType.CLOSED_BRACKET) openParens--
            if (tokens[index].type == TokenType.CLOSED_CURLY) openParens--

            if (openParens == 0) {
                index++
                break
            }

            if (tokens[index].type == TokenType.COMMA && openParens == 1) {
                listValueTokens.add(deepCopy(currentValueTokens))
                currentValueTokens.clear()
                currentKeyTokens.clear()

                index++
                continue
            }

            if (tokens[index].type == TokenType.COLON && openParens == 1) {
                listKeyTokens.add(deepCopy(currentKeyTokens))
                currentKeyTokens.clear()
                currentValueTokens.clear()

                index++
                continue
            }

            if (openParens == 1 && tokens[index].type == TokenType.OPEN_CURLY) {
                index++
                continue
            }
            currentKeyTokens.add(tokens[index])
            currentValueTokens.add(tokens[index])

            index++
        }
        index++

        if (currentKeyTokens.isNotEmpty()) listKeyTokens.add(currentKeyTokens)
        if (currentValueTokens.isNotEmpty()) listValueTokens.add(currentValueTokens)

        for ((index, t) in listKeyTokens.withIndex()) {
            if (index >= listValueTokens.size) break
            map[Evaluator.evaluateTree(parseTokens(t))] = Evaluator.evaluateTree(parseTokens(listValueTokens[index]))
        }

        return parseOtherStuff(TreeNode("dict", value = map))
    }

    private fun parseList(): TreeNode {
        val list = mutableListOf<Any>()

        val currentTokens = mutableListOf<Token>()
        val listTokens = mutableListOf<MutableList<Token>>()

        var openParens = 0

        while (index < tokens.size) {
            if (tokens[index].type == TokenType.OPEN_PARENTHESIS) openParens++
            if (tokens[index].type == TokenType.OPEN_BRACKET) openParens++
            if (tokens[index].type == TokenType.OPEN_CURLY) openParens++
            if (tokens[index].type == TokenType.CLOSED_PARENTHESIS) openParens--
            if (tokens[index].type == TokenType.CLOSED_BRACKET) openParens--
            if (tokens[index].type == TokenType.CLOSED_CURLY) openParens--

            if (openParens == 0) {
                index++
                break
            }

            if (tokens[index].type == TokenType.COMMA && openParens == 1) {
                listTokens.add(deepCopy(currentTokens))
                currentTokens.clear()

                index++
                continue
            }

            if (openParens == 1 && tokens[index].type == TokenType.OPEN_BRACKET) {
                index++
                continue
            }

            currentTokens.add(tokens[index])

            index++
        }
        if (currentTokens.isNotEmpty()) listTokens.add(currentTokens)

        for (t in listTokens) {
            list.add(Evaluator.evaluateTree(parseTokens(t)))
        }

        return parseOtherStuff(TreeNode("list", value = list))
    }

    private fun parseExpression(): TreeNode {
        var leftNode = parseEqualsTernary()

        while (index < tokens.size && tokens[index].type == TokenType.ASSIGN) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseEqualsTernary()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseEqualsTernary(): TreeNode {
        var leftNode = parseColon()

        while (index < tokens.size && (tokens[index].type == TokenType.EQUALS || tokens[index].type == TokenType.NOT_EQUALS || tokens[index].type == TokenType.LESS || tokens[index].type == TokenType.LESS_EQUAL || tokens[index].type == TokenType.GREATER_EQUAL|| tokens[index].type == TokenType.GREATER || tokens[index].type == TokenType.TERNARY)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseColon()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseColon(): TreeNode {
        var leftNode = parseAdditionAndSubtraction()

        while (index < tokens.size && (tokens[index].type == TokenType.COLON)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseAdditionAndSubtraction()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseAdditionAndSubtraction(): TreeNode {
        var leftNode = parseTerm()

        while (index < tokens.size && (tokens[index].type == TokenType.ADDITION || tokens[index].type == TokenType.SUBTRACTION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseTerm()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseTerm(): TreeNode {
        var leftNode = parseImplicitMultipliction()

        while (index < tokens.size && (tokens[index].type == TokenType.MULTIPLICATION || tokens[index].type == TokenType.DIVISION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseImplicitMultipliction()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseImplicitMultipliction(): TreeNode {
        var leftNode = parseExponetiation()

        while (index < tokens.size && (tokens[index].type == TokenType.IMPLICIT_MULTIPLICATION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseExponetiation()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseExponetiation(): TreeNode {
        var leftNode = parseCoalescing()

        while (index < tokens.size && (tokens[index].type == TokenType.EXPONENTIATION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseCoalescing()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseCoalescing(): TreeNode {
        var leftNode = parseFactor()

        while (index < tokens.size && (tokens[index].type == TokenType.COALESCING)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseFactor()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseFactor(): TreeNode {
        if (tokens[index].type == TokenType.NUMBER) {
            return parseNumber()
        } else if (tokens[index].type == TokenType.STRING) {
            return parseString()
        } else if (tokens[index].type == TokenType.IDENTIFIER) {
            return parseIdentifier()
        } else if (tokens[index].type == TokenType.FUNCTION_CALL) {
            return parseFunction()
        } else if (tokens[index].type == TokenType.OPEN_CURLY) {
            return parseDictionary()
        } else if (tokens[index].type == TokenType.OPEN_BRACKET) {
            return parseList()
        } else if (tokens[index].type == TokenType.UNDEFINED) {
            return parseUndefined()
        } else if (tokens[index].type == TokenType.TRUE) {
            return parseTrue()
        } else if (tokens[index].type == TokenType.FALSE) {
            return parseFalse()
        } else if (tokens[index].type == TokenType.ENDISTIC) {
            return parseEndistic()
        } else if (tokens[index].type == TokenType.IF) {
            return parseIf()
        } else if (tokens[index].type == TokenType.OPEN_PARENTHESIS) {
            index++
            val expressionNode = parseExpression()
            if (index < tokens.size && tokens[index].type == TokenType.CLOSED_PARENTHESIS) {
                index++
                return parseOtherStuff(expressionNode)
            } else throw IllegalArgumentException("Expected closing parenthesis")
        } else {
            if (tokens[index].type == TokenType.SUBTRACTION) {
                val nextIndex = index + 1
                if (nextIndex < tokens.size) {
                    val nextToken = tokens[nextIndex]
                    if (nextToken.type == TokenType.NUMBER) {
                        tokens.removeAt(index)
                        tokens.removeAt(index)
                        tokens.add(index, Token(
                            TokenType.NUMBER,
                            -((nextToken.value as Number).toDouble())
                        )
                        )

                        return parseFactor()
                    } else if (nextToken.type == TokenType.IDENTIFIER || nextToken.type == TokenType.FUNCTION_CALL || nextToken.type == TokenType.OPEN_PARENTHESIS) {
                        tokens.add(index, Token(
                            TokenType.NUMBER,
                            0.0
                        )
                        )

                        return parseOtherStuff(parseFactor())
                    }
                }
            }
            throw IllegalArgumentException("Unexpected token: ${tokens[index].type.id}")
        }
    }

    companion object {
        fun parseTokens(tokens: MutableList<Token>): TreeNode {
            if (tokens.isEmpty()) return TreeNode("number", value = 0.0)

            var openParen = 0
            var openBracket = 0
            var openCurly = 0

            val operationTokenTypes = listOf(
                TokenType.ADDITION, TokenType.MULTIPLICATION, TokenType.SUBTRACTION, TokenType.DIVISION, TokenType.EXPONENTIATION, TokenType.IMPLICIT_MULTIPLICATION, TokenType.FACTORIAL,
                TokenType.OPEN_PARENTHESIS, TokenType.CLOSED_PARENTHESIS, TokenType.OPEN_BRACKET, TokenType.CLOSED_BRACKET, TokenType.OPEN_CURLY, TokenType.CLOSED_CURLY,
                TokenType.FUNCTION_CALL, TokenType.CLASS_FUNCTION_CALL, TokenType.COMMA,
                TokenType.ASSIGN, TokenType.TERNARY, TokenType.WHITESPACE, TokenType.COALESCING, TokenType.COLON, TokenType.IF, TokenType.ELSE,
                TokenType.EQUALS, TokenType.GREATER_EQUAL, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.LESS
            )
            val valueTokenTypes = listOf(
                TokenType.NUMBER, TokenType.STRING
            )

            val whiteSpaceFreeTokens = tokens.mapNotNull { if (it.type == TokenType.WHITESPACE || it.type == TokenType.UNKNOWN_SYMBOL) null else it }

            val newTokens: MutableList<Token> = mutableListOf()
            for ((index, token) in whiteSpaceFreeTokens.withIndex()) {
                if (token.type == TokenType.OPEN_PARENTHESIS) openParen++
                if (token.type == TokenType.OPEN_BRACKET) openBracket++
                if (token.type == TokenType.OPEN_CURLY) openCurly++

                if (token.type == TokenType.CLOSED_PARENTHESIS) openParen--
                if (token.type == TokenType.CLOSED_BRACKET) openBracket--
                if (token.type == TokenType.CLOSED_CURLY) openCurly--

                val nextIndex = index + 1
                if (whiteSpaceFreeTokens.size <= nextIndex) {
                    newTokens.add(token)
                    break
                }
                val nextToken = whiteSpaceFreeTokens[nextIndex]
                newTokens.add(token)
                val addMul =
                    (!operationTokenTypes.contains(token.type) && (nextToken.type == TokenType.OPEN_PARENTHESIS || nextToken.type == TokenType.IDENTIFIER || nextToken.type == TokenType.FUNCTION_CALL)) ||
                            (token.type == TokenType.IDENTIFIER && (!operationTokenTypes.contains(nextToken.type) || nextToken.type == TokenType.OPEN_PARENTHESIS)) ||
                            (token.type == TokenType.CLOSED_PARENTHESIS && (!operationTokenTypes.contains(nextToken.type) || nextToken.type == TokenType.OPEN_PARENTHESIS ||  nextToken.type == TokenType.FUNCTION_CALL)) ||
                            (valueTokenTypes.contains(token.type) && valueTokenTypes.contains(nextToken.type))

                if (addMul) {
                    newTokens.add(
                        Token(
                        TokenType.IMPLICIT_MULTIPLICATION,
                        "*"
                    )
                    )
                }
            }

            while (openParen > 0) {
                newTokens.add(Token(TokenType.CLOSED_PARENTHESIS, ")"))
                openParen--
            }

            while (openBracket > 0) {
                newTokens.add(Token(TokenType.CLOSED_BRACKET, "]"))
                openBracket--
            }

            while (openCurly > 0) {
                newTokens.add(Token(TokenType.CLOSED_CURLY, "}"))
                openCurly--
            }

            return NodeParser(newTokens).parseExpression()
        }
    }

}