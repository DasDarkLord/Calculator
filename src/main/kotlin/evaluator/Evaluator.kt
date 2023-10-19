package evaluator

import calcConstants.userConstants
import calcFunctions.userFunctions
import parser.TreeNode

class Evaluator {
    companion object {
        private val types = ArrayList(listOf(
            NumberEvaluationType, StringEvaluationType, DictionaryEvaluationType, ListEvaluationType, UndefinedEvaluationType,
            AddEvaluationType, SubEvaluationType, MulEvaluationType, DivEvaluationType, PowEvaluationType, ModulusEvaluationType,
            IdEvaluationType, FunctionEvaluationType, ClassFunctionEvaluationType,
            EqualsEvaluationType, IndexEvaluationType, FactorialEvaluationType, CoalescingEvaluationType
        ))

        fun addEvalType(type: EvaluationType) {
            types.add(type)
        }

        fun evaluateTree(tree: TreeNode, constants: Map<List<String>, Any>? = null): Any {
            val uConsts = userConstants
            if (constants != null) {
                userConstants.putAll(constants)
            }

            for (type in types) {
                if (type.forType == tree.type || (type.aliases.contains(tree.type))) {
                    try {
                        return type.evaluate(tree)
                    } finally {
                        if (constants != null) {
                            userConstants.clear()
                            userConstants.putAll(uConsts)
                        }
                    }
                }
            }

            if (constants != null) {
                userConstants.clear()
                userConstants.putAll(uConsts)
            }

            throw IllegalStateException("Unknown tree type ${tree.type}")
        }
    }
}