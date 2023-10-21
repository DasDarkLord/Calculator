package evaluator

import calcConstants.constantExists
import calcConstants.constants
import calcConstants.userConstants
import calcFunctions.*
import calcFunctions.argumentSet.PatternSetReader
import parser.TreeNode
import prettierVersion
import utils.multifactorial
import utils.numToDouble
import java.lang.Exception
import kotlin.math.pow

abstract class LeftRightEvaluationType : EvaluationType {
    abstract fun evaluate(left: TreeNode, right: TreeNode, label: String): Any
    override fun evaluate(tree: TreeNode, label: String): Any {
        if (tree.left == null || tree.right == null) return 0
        return evaluate(tree.left, tree.right, label)
    }

    override val aliases: List<String>
        get() = emptyList()
}

abstract class ValueEvaluationType : EvaluationType {
    abstract fun evaluate(value: Any, label: String): Any
    override fun evaluate(tree: TreeNode, label: String): Any {
        if (tree.value == null) return 0
        return evaluate(tree.value, label)
    }

    override val aliases: List<String>
        get() = emptyList()
}

interface EvaluationType {
    val forType: String
    val aliases: List<String>
    fun evaluate(tree: TreeNode, label: String): Any
}


// Implement default types


// Values
object NumberEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return numToDouble(value)
    }

    override val forType: String
        get() = "number"

}

object StringEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return value.toString()
    }

    override val forType: String
        get() = "string"

}

object IdEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        val combinedMaps = userConstants
        for (constant in constants) combinedMaps[constant.key] = constant.value

        val strValue = value.toString()
        for (constantEntry in combinedMaps) {

            for (name in constantEntry.key) {
                if (strValue == name) return constantEntry.value
            }
        }

        return Undefined
    }

    override val forType: String
        get() = "id"

}

object FunctionEvaluationType : EvaluationType {
    override val forType: String
        get() = "func_call"
    override val aliases: List<String>
        get() = emptyList()

    override fun evaluate(tree: TreeNode, label: String): Any {
        val objs = mutableListOf<TreeNode>()
        for (node in tree.arguments!!) objs.add(node)

        val value = tree.value

        val combinedMaps = userFunctions
        for (constant in functions) combinedMaps[constant.key] = constant.value

        val strValue = value.toString()
        for (constantEntry in combinedMaps) {
            for (name in constantEntry.key) {
                if (strValue == name) {
                    try {
                        val func = constantEntry.value
                        val reader = PatternSetReader(func.patternSet)
                        reader.readObjects(objs)
                        return func.execute(reader.set)
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                    }
                }
            }
        }

        return Undefined
    }
}

object ClassFunctionEvaluationType : EvaluationType {
    override val forType: String
        get() = "func_call0"
    override val aliases: List<String>
        get() = emptyList()

    override fun evaluate(tree: TreeNode, label: String): Any {
        val affected =
            if (tree.left!!.type == "id") IdEvaluationType.evaluate(tree.left, "id")
            else Evaluator.evaluateTree(tree.left)

        val functionTree = tree.right!!

        val objs = mutableListOf<TreeNode>()
        for (node in functionTree.arguments!!) objs.add(node)
        val value = functionTree.value

        val strValue = value.toString()
        for (constantEntry in classFunctions) {
            for (name in constantEntry.key) {
                if (strValue == name) {
                    for (func in constantEntry.value) {
                        try {
                            if (!func.forClass.isInstance(affected)) continue

                            val reader = PatternSetReader(func.patternSet)
                            reader.readObjects(objs)

                            val newVal = func.execute(affected, reader.set)
                            if (tree.left.type == "id") {
                                if (!constants.containsKey(tree.left.value!!)) {
                                    userConstants[listOf(tree.left.value as String)] = newVal
                                }
                            }

                            return newVal
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }
                }
            }
        }

        System.err.println("Failed to execute method " + tree.value!!)
        return Undefined
    }
}

object DictionaryEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return value as MutableMap<*, *>
    }

    override val forType: String
        get() = "dict"
}

object ListEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return value as MutableList<*>
    }

    override val forType: String
        get() = "list"
}

object EqualsEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        return Evaluator.evaluateTree(left) == Evaluator.evaluateTree(right)
    }

    override val forType: String
        get() = "eq0"

}

object AssignEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        var type = "assignLeft"
        if (left.type == "id") {
            if (constantExists(left.value!! as String)) type = "checkEquals"
        } else {
            if (left.type == "index" && left.left!!.type == "id") {
                if (constantExists(left.left.value!! as String)) type = "checkEquals"
            }
        }

        if (left.type == "func_call") {
            if (functionExists(left.value!! as String)) type = "checkEquals"
        }

        if (type == "assignLeft" && !(left.type == "id" || left.type == "index" || left.type == "func_call")) type = "checkEquals"

        if (type == "assignLeft") {
            if (left.type == "index") {
                val value = Evaluator.evaluateTree(right)

                var id = left.left!!
                if (id.type == "index") {
                    return AssignEvaluationType.evaluate(id, label)
                }

                val toChange = left.right!!.value
                val cVal = userConstants[listOf(id.value!! as String)]
                if (cVal is MutableMap<*, *>) (cVal as MutableMap<Any, Any>)[toChange as String] = value
                if (cVal is MutableList<*>) (cVal as MutableList<Any>)[(toChange as Number).toInt()] = value

                userConstants[listOf(id.value as String)] = cVal as Any
                return mapOf(
                    id.value to cVal
                )
            }

            val id = left.value!!

            if (left.type == "func_call") {
                val argNames = mutableListOf<String>()
                for (argument in left.arguments!!) {
                    if (argument.type == "id") argNames.add(argument.value!! as String)
                    if (argument.type == "string") argNames.add(argument.value!! as String)
                }

                userFunctions[listOf(id as String)] = UserFunction(right, argNames)

                return mapOf(
                    id to userFunctions[listOf(id)]
                )
            }

            val value = Evaluator.evaluateTree(right)

            userConstants[listOf(id as String)] = value
            return mapOf(
                id to value
            )
        }

        return (Evaluator.evaluateTree(left) == Evaluator.evaluateTree(right))
    }

    override val forType: String
        get() = "eq"
}

object TernaryEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        if (right.type != "colon") return Undefined

        val condition = Evaluator.evaluateTree(left)

        val l = Evaluator.evaluateTree(right.left!!)
        val r = Evaluator.evaluateTree(right.right!!)

        return if (condition !is Boolean) Undefined
        else if (condition) l else r
    }

    override val forType: String
        get() = "ternary"

}

object CoalescingEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val leftEval = Evaluator.evaluateTree(left)
        if (leftEval is Undefined) return Evaluator.evaluateTree(right)

        return leftEval
    }

    override val forType: String
        get() = "coalescing"
}

object IndexEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Map<*, *>) {
            if (!l.containsKey(r)) {
                return Undefined
            }
            return l[r]!!
        }
        if (l is List<*> || l is String) {
            val list: List<*> = if (l is String) l.toCharArray().asList() else l as List<*>
            if (r !is Number) {
                return Undefined
            }
            if (r.toInt() >= list.size) {
                return Undefined
            }
            if (r.toInt() < 0) {
                return Undefined
            }
            return list[r.toInt()]!!
        }

        return Undefined
    }

    override val forType: String
        get() = "index"
}

object UndefinedEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return Undefined
    }

    override val forType: String
        get() = "undefined"
}

object TrueEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return true
    }

    override val forType: String
        get() = "true"

}

object FalseEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any, label: String): Any {
        return false
    }

    override val forType: String
        get() = "false"

}

// Operations
object AddEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (r is Number && l is Number) {
            return r.toDouble() + l.toDouble()
        }
        if (r is String || l is String) {
            return prettierVersion(l) + prettierVersion(r)
        }

        return Undefined
    }

    override val forType: String
        get() = "add"

}

object SubEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (r is Number && l is Number) {
            return l.toDouble() - r.toDouble()
        }
        if (r is String || l is String) {
            return l.toString().replace(Regex(r.toString()), "")
        }

        return Undefined
    }

    override val forType: String
        get() = "sub"

}

object MulEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() * r.toDouble()
        if (l is String || r is String) {
            val times = if (l is Number) l.toDouble() else if (r is Double) r.toDouble() else Double.NaN
            return if (times.isNaN()) {
                l.toString() + r
            } else {
                if (l is Number) r.toString().repeat(l.toInt())
                else l.toString().repeat((r as Number).toInt())
            }
        }

        return Undefined
    }

    override val forType: String
        get() = "mul"

    override val aliases: List<String>
        get() = listOf("imul")

}

object DivEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() / r.toDouble()
        if (r is String || l is String) {
            return l.toString().replace(r.toString(), "")
        }
        return Undefined
    }

    override val forType: String
        get() = "div"

}

object PowEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble().pow(r.toDouble())
        return Undefined
    }

    override val forType: String
        get() = "pow"

}

object ModulusEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() % r.toDouble()
        return Undefined
    }

    override val forType: String
        get() = "mod"
}

object FactorialEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode, label: String): Any {
        var number = Evaluator.evaluateTree(left)
        val factorial = Evaluator.evaluateTree(right) as Double
        if (number !is Number) return 0
        number = number.toDouble()

        return multifactorial(number, factorial)
    }

    override val forType: String
        get() = "factorial"

}