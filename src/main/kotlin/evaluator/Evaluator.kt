package evaluator

import parser.TreeNode

class Evaluator {
    companion object {
        private val types = listOf(
            NumberEvaluationType, StringEvaluationType, TrueEvaluationType, FalseEvaluationType, UndefinedEvaluationType,
            AddEvaluationType, SubEvaluationType, MulEvaluationType, DivEvaluationType, PowEvaluationType, ModulusEvaluationType, FactorialEvaluationType,
            IfEvaluationType,
            IndexEvaluationType, CoalescingEvaluationType, TernaryEvaluationType, AssignEvaluationType,
            LessEqualsEvaluationType, GreaterEvaluationType, LessEvaluationType, GreaterEqualsEvaluationType, NotEqualsEvaluationType, EqualsEvaluationType,
            ListEvaluationType, DictionaryEvaluationType,
            ClassFunctionEvaluationType, FunctionEvaluationType,
            IdEvaluationType
        )

        fun evaluateTree(tree: TreeNode, constants: Map<List<String>, Any>? = null): Any {
            val uConsts = calcConstants.constants
            if (constants != null) {
                calcConstants.constants.putAll(constants)
            }

            for (type in types) {
                if (type.forType == tree.type || (type.aliases.contains(tree.type))) {
                    try {
                        return type.evaluate(tree, tree.type)
                    } finally {
                        if (constants != null) {
                            calcConstants.constants.clear()
                            calcConstants.constants.putAll(uConsts)
                        }
                    }
                }
            }

            if (constants != null) {
                calcConstants.constants.clear()
                calcConstants.constants.putAll(uConsts)
            }

            throw IllegalStateException("Unknown tree type ${tree.type}")
        }
    }
}