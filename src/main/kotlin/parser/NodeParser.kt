package parser

import calcConstants.constants
import evaluator.Evaluator
import lexer.Token
import lexer.TokenType
import utils.deepCopy
import kotlin.math.exp

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
        return TreeNode(
            "func_call",
            value = token.value,
            arguments = treeArguments
        )
    }

    private fun parseOtherStuff(node: TreeNode): TreeNode {
        var isNodeChanged = true
        var currentNode = node

        while (isNodeChanged) {
            val newNode = parseFactorial(parseIndex(parseClassFunction(currentNode)))
            isNodeChanged = currentNode != newNode
            currentNode = newNode
        }

        return currentNode
    }

    private fun parseIndex(node: TreeNode): TreeNode {
        if (index < tokens.size) {
            if (tokens[index].type == TokenType.OPEN_BRACKET) {
                index++

                val expr = parseExpression()
                if (tokens[index].type == TokenType.CLOSED_BRACKET) {
                    index++
                    return parseIndex(
                        TreeNode(
                            "index",
                            left = node,
                            right = expr
                        )
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

            if (factorial == 0) return node
            else {
                return TreeNode(
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
        if (index >= tokens.size) return node
        val token = tokens[index]
        if (token.type != TokenType.CLASS_FUNCTION_CALL) return node

        return TreeNode(
            token.type.id,
            left = node,
            right = parseFunction()
        )
    }

    private fun parseUndefined(): TreeNode {
        val token = tokens[index]
        index++
        return TreeNode("undefined", value = token.value)
    }

    private fun parseNumber(): TreeNode {
        val token = tokens[index]
        index++
        return parseOtherStuff(TreeNode("number", value = token.value))
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
        if (currentKeyTokens.isNotEmpty()) listKeyTokens.add(currentKeyTokens)
        if (currentValueTokens.isNotEmpty()) listValueTokens.add(currentValueTokens)

        index++

        for ((index, t) in listKeyTokens.withIndex()) {
            if (index >= listValueTokens.size) break
            map[Evaluator.evaluateTree(parseTokens(t))] = Evaluator.evaluateTree(parseTokens(listValueTokens[index]))
        }

        return TreeNode("dict", value = map)
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

        index++

        for (t in listTokens) {
            list.add(Evaluator.evaluateTree(parseTokens(t)))
        }

        return parseOtherStuff(TreeNode("list", value = list))
    }

    private fun parseExpression(): TreeNode {
        var leftNode = parseAdditionAndSubtraction()

        while (index < tokens.size && (tokens[index].type == TokenType.EQUALS)) {
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
        var leftNode = parseExponetiation()

        while (index < tokens.size && (tokens[index].type == TokenType.MULTIPLICATION || tokens[index].type == TokenType.DIVISION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseExponetiation()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseExponetiation(): TreeNode {
        var leftNode = parseImplicitMultipliction()

        while (index < tokens.size && (tokens[index].type == TokenType.EXPONENTIATION)) {
            val operator = tokens[index].type.id
            index++
            val rightNode = parseImplicitMultipliction()
            leftNode = TreeNode(operator, leftNode, rightNode)
        }

        return leftNode
    }

    private fun parseImplicitMultipliction(): TreeNode {
        var leftNode = parseCoalescing()

        while (index < tokens.size && (tokens[index].type == TokenType.IMPLICIT_MULTIPLICATION)) {
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
        } else if (tokens[index].type == TokenType.OPEN_PARENTHESIS) {
            index++
            val expressionNode = parseExpression()
            if (index < tokens.size && tokens[index].type == TokenType.CLOSED_PARENTHESIS) {
                index++
                return parseFactorial(parseIndex(expressionNode))
            } else throw IllegalArgumentException("Expected closing parenthesis")
        } else {
            if (tokens[index].type == TokenType.SUBTRACTION) {
                val nextIndex = index + 1
                if (nextIndex < tokens.size) {
                    val nextToken = tokens[nextIndex]
                    if (nextToken.type == TokenType.NUMBER) {
                        tokens.removeAt(index)
                        tokens.add(index, Token(
                            TokenType.NUMBER,
                            -((nextToken.value as Number).toDouble())
                        ))
                        return parseOtherStuff(parseFactor())
                    } else if (nextToken.type == TokenType.IDENTIFIER || nextToken.type == TokenType.FUNCTION_CALL || nextToken.type == TokenType.OPEN_PARENTHESIS) {
                        tokens.add(index, Token(
                            TokenType.NUMBER,
                            0.0
                        ))
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
                TokenType.ADDITION, TokenType.MULTIPLICATION, TokenType.SUBTRACTION, TokenType.DIVISION, TokenType.EXPONENTIATION, TokenType.IMPLICIT_MULTIPLICATION,
                TokenType.OPEN_PARENTHESIS, TokenType.CLOSED_PARENTHESIS, TokenType.OPEN_BRACKET, TokenType.CLOSED_BRACKET, TokenType.OPEN_CURLY, TokenType.CLOSED_CURLY,
                TokenType.FUNCTION_CALL, TokenType.CLASS_FUNCTION_CALL, TokenType.COMMA,
                TokenType.EQUALS, TokenType.WHITESPACE, TokenType.COALESCING, TokenType.COLON
            )
            val valueTokenTypes = listOf(
                TokenType.NUMBER, TokenType.STRING
            )

            val whiteSpaceFreeTokens = mutableListOf<Token>()
            for (token in tokens) {
                if (token.type != TokenType.WHITESPACE) whiteSpaceFreeTokens.add(token)
            }

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
                    newTokens.add(Token(
                        TokenType.IMPLICIT_MULTIPLICATION,
                        "*"
                    ))
                }
            }

            while (openParen > 0) {
                newTokens.add(Token(TokenType.CLOSED_PARENTHESIS, ")"))
                openParen--
            }

            while (openBracket > 0) {
                newTokens.add(Token(TokenType.CLOSED_BRACKET, ")"))
                openParen--
            }

            while (openCurly > 0) {
                newTokens.add(Token(TokenType.CLOSED_CURLY, ")"))
                openParen--
            }

            return NodeParser(newTokens).parseExpression()
        }
    }

}