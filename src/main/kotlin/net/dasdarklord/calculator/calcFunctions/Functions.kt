package net.dasdarklord.calculator.calcFunctions

import net.dasdarklord.calculator.calcFunctions.argumentSet.ArgumentSet
import net.dasdarklord.calculator.calcFunctions.patternSet.PatternSet
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.AnyArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.BooleanArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.NumberArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.StringArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.TreeNodeArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.SingletonNode
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.VarargsNode
import net.dasdarklord.calculator.evaluator.ClassFunctionEvaluationType
import net.dasdarklord.calculator.evaluator.Evaluator
import net.dasdarklord.calculator.evaluator.FunctionEvaluationType
import net.dasdarklord.calculator.evaluator.IdEvaluationType
import net.dasdarklord.calculator.lexer.Lexer
import net.dasdarklord.calculator.lexer.TokenType
import net.dasdarklord.calculator.parser.NodeParser
import net.dasdarklord.calculator.parser.TreeNode
import net.dasdarklord.calculator.utils.*
import net.dasdarklord.calculator.prettierVersion
import kotlin.math.*

val functions = mapOf(
    listOf("abs", "absolute") to AbsFunction,
    listOf("sum", "summation") to SumFunction,
    listOf("sin", "sine") to SinFunction,
    listOf("asin", "arcsin", "arcsine", "asine") to AsinFunction,
    listOf("sinh", "sineh") to SinhFunction,
    listOf("asinh", "arcsineh", "arcsinh", "asineh") to AsinhFunction,
    listOf("cos", "cosgent") to CosFunction,
    listOf("acos", "arccos", "acosine", "arccosine") to AcosFunction,
    listOf("cosh", "cosineh") to CoshFunction,
    listOf("acosh", "acosineh", "arccosineh", "arccosh") to AcoshFunction,
    listOf("tan", "tangent") to TanFunction,
    listOf("atan", "arctan", "atangent", "arctangent") to AtanFunction,
    listOf("atan2") to Atan2Function,
    listOf("tanh", "tangenth") to TanhFunction,
    listOf("atanh", "atangenth", "arctangenth", "arctanh") to AtanhFunction,
    listOf("sign", "signum") to SignFunction,
    listOf("s", "successor") to SuccessorFunction,
    listOf("root") to RootFunction,
    listOf("sqrt") to SqrtFunction,
    listOf("cbrt") to CbrtFunction,
    listOf("log", "logarithm") to LogFunction,
    listOf("log2", "logarithm2") to Log2Function,
    listOf("log10", "logarithm10") to Log10Function,
    listOf("ln", "logn", "logarithmn", "login") to LogNFunction,
    listOf("sec", "secant") to SecantFunction,
    listOf("sech", "secanth") to HyperbolicSecantFunction,
    listOf("csec", "cosec", "cosecant", "csecant") to CosecantFunction,
    listOf("cotan", "ctan", "cotangent", "ctangent") to CotangentFunction,
    listOf("cotanh", "canh", "cotangenth", "ctangenth") to HyperbolicCotangentFunction,
    listOf("csech", "cosech", "cosecanth", "csecanth") to HyperbolicCoSecantFunction,
    listOf("len", "length") to LengthFunction,
    listOf("multifactorial", "multifact", "mfact") to MultiFactorialFunction,
    listOf("factorial", "fact") to FactorialFunction,
    listOf("factorial2", "fact2") to Factorial2Function,
    listOf("round") to RoundFunction,
    listOf("parseNumber", "parsenum", "parseNum", "parsenumber", "num", "number") to ParseNumberFunction,
    listOf("str", "string") to AsStringFunction,
    listOf("charCode") to CharCodeFunction,
    listOf("bool", "boolean") to BooleanFunction,
    listOf("Regex", "RegEx", "regex", "regEx", "regularEx", "regularex", "regularExpression", "regularexpression") to RegexFunction,
    listOf("rand", "random") to RandomFunction,
    listOf("boundedRandom", "boundedRand", "boundedrandom", "boundedrand", "boundrand", "boundrandom", "boundRand", "boundRandom") to BoundedRandomFunction,
    listOf("identifier", "id", "var", "variable") to IdentifierFunction,
    listOf("invoke") to InvokeFunction,
    listOf("invokeTo") to InvokeClassFunction,
    listOf("write", "print") to WriteFunction,
    listOf("writeln", "println") to WriteLnFunction,
    listOf("eval") to EvalFunction,
    listOf("gamma") to GammaFunction,
    listOf("integrate", "integral") to IntegrateFunction,
)

fun functionExists(name: String): Boolean {
    for (func in functions) {
        for (funcName in func.key) if (funcName == name) return true
    }

    return false
}

val userFunctions = HashMap<List<String>, CalcFunc>()

interface CalcFunc {
    val patternSet: PatternSet
    fun execute(argumentSet: ArgumentSet): Any
}

// User Function

class UserFunction(private val expression: TreeNode, private val arguments: List<String>) : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(VarargsNode("arguments", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val functionConstants = mutableMapOf<List<String>, Any>()

        for ((index, arg) in argumentSet.getVarargValue<Any>("arguments").withIndex()) {
            val argName = arguments[index]
            functionConstants[listOf(argName)] = arg
        }

        return Evaluator.evaluateTree(expression, functionConstants)
    }

    override fun toString(): String {
        return "user defined function"
    }
}

// Default Functions

object AbsFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val num = argumentSet.getValue<Double>("number")
        return abs(num)
    }
}

object SinFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sin(argumentSet.getValue("number"))
    }
}

object AsinFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return asin(argumentSet.getValue("number"))
    }
}

object SinhFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sinh(argumentSet.getValue("number"))
    }
}

object AsinhFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return asinh(argumentSet.getValue("number"))
    }
}

object CosFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return cos(argumentSet.getValue("number"))
    }
}

object AcosFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return acos(argumentSet.getValue("number"))
    }
}

object CoshFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return cosh(argumentSet.getValue("number"))
    }
}

object AcoshFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return acosh(argumentSet.getValue("number"))
    }
}

object TanFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return tan(argumentSet.getValue("number"))
    }
}

object AtanFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return atan(argumentSet.getValue("number"))
    }
}

object TanhFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return tanh(argumentSet.getValue("number"))
    }
}

object AtanhFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return atanh(argumentSet.getValue("number"))
    }
}

object SignFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sign(argumentSet.getValue("number"))
    }
}

object SuccessorFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return argumentSet.getValue<Double>("number") + 1.0
    }
}

object SumFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("expression", TreeNodeArgument()))
            .addElement(SingletonNode("min", NumberArgument()))
            .addElement(SingletonNode("max", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        var min = argumentSet.getValue<Number>("min").toInt()
        var max = argumentSet.getValue<Number>("max").toInt()
        if (min > max) {
            val temp = min
            min = max
            max = temp
        }

        val expression = argumentSet.getValue<TreeNode>("expression")

        var sum = 0.0
        for (i in min..max) {
            val evaluated = Evaluator.evaluateTree(expression, mapOf(listOf("x", "X") to i))
            if (evaluated is Number) sum += evaluated.toDouble()
        }

        return sum
    }
}

object RootFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))
            .addElement(SingletonNode("index", NumberArgument())
                            .setOptional(2.0))

    override fun execute(argumentSet: ArgumentSet): Any {
        val number = argumentSet.getValue<Double>("number")
        val index = argumentSet.getValue<Double>("index")

        if (index == 2.0) return sqrt(number)
        if (index == 3.0) return cbrt(number)

        return number.pow(1.0 / index)
    }

}

object SqrtFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sqrt(argumentSet.getValue("number"))
    }

}

object CbrtFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return cbrt(argumentSet.getValue("number"))
    }

}

object Log10Function : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return log10(argumentSet.getValue("number"))
    }

}

object Log2Function : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return log2(argumentSet.getValue("number"))
    }

}

object LogFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))
            .addElement(SingletonNode("base", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return log(argumentSet.getValue("number"), argumentSet.getValue("base"))
    }

}

object LogNFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return ln(argumentSet.getValue("number"))
    }

}

object CotangentFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return cot(argumentSet.getValue("number"))
    }

}

object HyperbolicCotangentFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return coth(argumentSet.getValue("number"))
    }

}

object SecantFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sec(argumentSet.getValue("number"))
    }

}

object HyperbolicSecantFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return sech(argumentSet.getValue("number"))
    }

}

object CosecantFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return csc(argumentSet.getValue("number"))
    }

}

object HyperbolicCoSecantFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return csch(argumentSet.getValue("number"))
    }

}

object LengthFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("value", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val value = argumentSet.getValue<Any>("value")
        if (value is String) return value.length
        if (value is List<*>) return value.size
        if (value is Map<*, *>) return value.size

        return value
    }

}

object MultiFactorialFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))
            .addElement(SingletonNode("factorial", NumberArgument())
                .setOptional(1.0))

    override fun execute(argumentSet: ArgumentSet): Any {
        return multifactorial(argumentSet.getValue("number"), argumentSet.getValue("factorial"))
    }

}

object FactorialFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return multifactorial(argumentSet.getValue("number"), 1.0)
    }

}

object Factorial2Function : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("number", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return multifactorial(argumentSet.getValue("number"), 2.0)
    }

}

object Atan2Function : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("y", NumberArgument()))
            .addElement(SingletonNode("x", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return atan2(argumentSet.getValue("y"), argumentSet.getValue("x"))
    }

}

object RoundFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("value", NumberArgument()))
            .addElement(SingletonNode("roundTo", NumberArgument())
                            .setOptional(1.0))

    override fun execute(argumentSet: ArgumentSet): Any {
        val value = argumentSet.getValue<Double>("value")
        val toRoundTo = argumentSet.getValue<Double>("roundTo")
        return (value / toRoundTo).roundToInt() * toRoundTo
    }

}

object ParseNumberFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("value", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return argumentSet.getValue<Any>("value").toString().toDouble()
    }

}

object AsStringFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("value", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return prettierVersion(argumentSet.getValue<Any>("value").toString())
    }

}

object RegexFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("expression", StringArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return Regex(argumentSet.getValue("expression"))
    }
}

object RandomFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()

    override fun execute(argumentSet: ArgumentSet): Any {
        return Math.random()
    }

}

object BoundedRandomFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("min", NumberArgument()))
            .addElement(SingletonNode("max", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val min = argumentSet.getValue<Double>("min")
        val max = argumentSet.getValue<Double>("max")
        return min + (Math.random() * (max - min))
    }

}

object IdentifierFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("name", StringArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return IdEvaluationType.evaluate(
            TreeNode(
            "id",
            value = argumentSet.getValue<String>("name")),
            "id"
        )
    }

}

object InvokeFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("name", StringArgument()))
            .addElement(VarargsNode("arguments", TreeNodeArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val arguments = mutableListOf<TreeNode>()
        for (argument in argumentSet.getVarargValue<TreeNode>("arguments")) {
            arguments.add(argument)
        }

        val tree = TreeNode(
            TokenType.FUNCTION_CALL.id,
            value = argumentSet.getValue<String>("name"),
            arguments = arguments
        )

        return FunctionEvaluationType.evaluate(tree, "invoke")
    }

}

object InvokeClassFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("affected", TreeNodeArgument()))
            .addElement(SingletonNode("name", StringArgument()))
            .addElement(VarargsNode("arguments", TreeNodeArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val arguments = mutableListOf<TreeNode>()
        for (argument in argumentSet.getVarargValue<TreeNode>("arguments")) {
            arguments.add(argument)
        }

        val functionTree = TreeNode(
            TokenType.FUNCTION_CALL.id,
            value = argumentSet.getValue<String>("name"),
            arguments = arguments
        )

        val tree = TreeNode(
            TokenType.CLASS_FUNCTION_CALL.id,
            left = argumentSet.getValue("affected"),
            right = functionTree
        )

        return ClassFunctionEvaluationType.evaluate(tree, "invokeTo")
    }

}

object WriteFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(VarargsNode("m", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        for (m in argumentSet.getVarargValue<Any>("m")) {
            print(m)
        }

        return 0
    }

}

object WriteLnFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(VarargsNode("m", AnyArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        for (m in argumentSet.getVarargValue<Any>("m")) {
            print(m)
        }
        println()

        return 0
    }

}

object CharCodeFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(VarargsNode("code", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val output = StringBuilder()
        for (num in argumentSet.getVarargValue<Number>("code")) {
            output.append(Char(num.toInt()))
        }
        return output.toString()
    }

}

object BooleanFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("value", StringArgument(), NumberArgument(), BooleanArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val value = argumentSet.getValue<Any>("value")
        if (value is Boolean) return value
        if (value is Number) return value.toInt() > 0
        if (value is String) return value == "true"
        return false
    }

}

object EvalFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("expression", StringArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val expression = argumentSet.getValue<String>("expression")
        val tokens = Lexer(expression).lexTokens()
        val node = NodeParser.parseTokens(tokens)
        return Evaluator.evaluateTree(node)
    }

}

object GammaFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("p", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        return gamma(argumentSet.getValue("p"))
    }

}

object IntegrateFunction : CalcFunc {
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("f", TreeNodeArgument()))
            .addElement(SingletonNode("a", NumberArgument()))
            .addElement(SingletonNode("b", NumberArgument()))

    override fun execute(argumentSet: ArgumentSet): Any {
        val f: (Double)->Double = {
            val r = Evaluator.evaluateTree(argumentSet.getValue("f"), mapOf(
                listOf("x", "X") to it
            ))

            if (r is Number) r.toDouble()
            else 0.0
        }

        return integrate(
            argumentSet.getValue("a"),
            argumentSet.getValue("b"),
            f
        )
    }

}