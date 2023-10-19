package evaluator

import calcConstants.constants
import calcConstants.userConstants
import calcFunctions.UserFunction
import calcFunctions.argumentSet.PatternSetReader
import calcFunctions.classFunctions
import calcFunctions.functions
import calcFunctions.userFunctions
import parser.TreeNode
import utils.multifactorial
import utils.numToDouble
import java.lang.Exception
import kotlin.math.pow

abstract class LeftRightEvaluationType : EvaluationType {
    abstract fun evaluate(left: TreeNode, right: TreeNode): Any
    override fun evaluate(tree: TreeNode): Any {
        if (tree.left == null || tree.right == null) return 0
        return evaluate(tree.left, tree.right)
    }

    override val aliases: List<String>
        get() = emptyList()
}

abstract class ValueEvaluationType : EvaluationType {
    abstract fun evaluate(value: Any): Any
    override fun evaluate(tree: TreeNode): Any {
        if (tree.value == null) return 0
        return evaluate(tree.value)
    }

    override val aliases: List<String>
        get() = emptyList()
}

interface EvaluationType {
    val forType: String
    val aliases: List<String>
    fun evaluate(tree: TreeNode): Any
}


// Implement default types


// Values
object NumberEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any): Any {
        return numToDouble(value)
    }

    override val forType: String
        get() = "number"

}

object StringEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any): Any {
        return value.toString()
    }

    override val forType: String
        get() = "string"

}

object IdEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any): Any {
        val combinedMaps = userConstants
        for (constant in constants) combinedMaps[constant.key] = constant.value

        val strValue = value.toString()
        for (constantEntry in combinedMaps) {

            for (name in constantEntry.key) {
                if (strValue == name) return constantEntry.value
            }
        }

        return value.toString()
    }

    override val forType: String
        get() = "id"

}

object FunctionEvaluationType : EvaluationType {
    override val forType: String
        get() = "func_call"
    override val aliases: List<String>
        get() = emptyList()

    override fun evaluate(tree: TreeNode): Any {
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

        return 0.0
    }
}

object ClassFunctionEvaluationType : EvaluationType {
    override val forType: String
        get() = "func_call0"
    override val aliases: List<String>
        get() = emptyList()

    override fun evaluate(tree: TreeNode): Any {
        val affected = IdEvaluationType.evaluate(tree.left!!)

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

                            func.execute(affected, reader.set)

                            return affected
                        } catch (ignored: Exception) {
                            ignored.printStackTrace()
                        }
                    }
                }
            }
        }

        error("Failed to execute method")
    }
}

object DictionaryEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any): Any {
        return value as MutableMap<*, *>
    }

    override val forType: String
        get() = "dict"
}

object ListEvaluationType : ValueEvaluationType() {
    override fun evaluate(value: Any): Any {
        return value as MutableList<*>
    }

    override val forType: String
        get() = "list"
}

object EqualsEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        var type = "assignLeft"
        if (left.type == "id") {
            if (constants.containsKey(left.value!!)) type = "checkEquals"
        } else {
            if (left.type == "index" && left.left!!.type == "id") {
                if (constants.containsKey(left.left.value!!)) type = "checkEquals"
            }
        }

        if (left.type == "func_call") {
            if (functions.containsKey(left.value!!)) type = "checkEquals"
        }

        if (type == "assignLeft") {
            val value = Evaluator.evaluateTree(right)

            if (left.type == "index") {
                var id = left.left!!
                if (id.type == "index") {
                    return EqualsEvaluationType.evaluate(id)
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

            userConstants[listOf(id as String)] = value
            return mapOf(
                id to value
            )
        }

        return (Evaluator.evaluateTree(left) == Evaluator.evaluateTree(right)).toString()
    }

    override val forType: String
        get() = "eq"
}

object IndexEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Map<*, *>) {
            if (!l.containsKey(r)) error("Map does not contain key $r")
            return l[r]!!
        }
        if (l is List<*> || l is String) {
            val list: List<*> = if (l is String) l.toCharArray().asList() else l as List<*>
            if (r !is Number) error("Index is not a number")
            if (r.toInt() >= list.size) error("Index is out of bounds (" + r + " >= " + list.size + ")")
            if (r.toInt() < 0) error("Index is smaller than 0")
            return list[r.toInt()]!!
        }

        return 0
    }

    override val forType: String
        get() = "index"
}

// Operations
object AddEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (r is Number && l is Number) {
            return r.toDouble() + l.toDouble()
        }
        if (r is String || l is String) {
            return l.toString() + r.toString()
        }

        return 0
    }

    override val forType: String
        get() = "add"

}

object SubEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (r is Number && l is Number) {
            return l.toDouble() - r.toDouble()
        }
        if (r is String || l is String) {
            return l.toString().replace(Regex(r.toString()), "")
        }

        return 0
    }

    override val forType: String
        get() = "sub"

}

object MulEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() * r.toDouble()
        if (l is String || r is String) {
            val times = if (l is Number) l.toDouble() else if (r is Double) r.toDouble() else Double.NaN
            return if (times.isNaN()) {
                l as String + r
            } else {
                if (l is Number) r.toString().repeat(l.toInt())
                else l.toString().repeat((r as Number).toInt())
            }
        }

        return 0
    }

    override val forType: String
        get() = "mul"

    override val aliases: List<String>
        get() = listOf("imul")

}

object DivEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() / r.toDouble()
        if (r is String || l is String) {
            return l.toString().replace(r.toString(), "")
        }
        return 0
    }

    override val forType: String
        get() = "div"

}

object PowEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble().pow(r.toDouble())
        return 0
    }

    override val forType: String
        get() = "pow"

}

object ModulusEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        val l = Evaluator.evaluateTree(left)
        val r = Evaluator.evaluateTree(right)
        if (l is Number && r is Number) return l.toDouble() % r.toDouble()
        return 0
    }

    override val forType: String
        get() = "mod"
}

object FactorialEvaluationType : LeftRightEvaluationType() {
    override fun evaluate(left: TreeNode, right: TreeNode): Any {
        var number = Evaluator.evaluateTree(left)
        val factorial = Evaluator.evaluateTree(right) as Double
        if (number !is Number) return 0
        number = number.toDouble()

        return multifactorial(number, factorial)
    }

    override val forType: String
        get() = "factorial"

}