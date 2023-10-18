package evaluator

import parser.TreeNode

class Evaluator {
    companion object {
        private val types = ArrayList(listOf(
            NumberEvaluationType, StringEvaluationType, DictionaryEvaluationType, ListEvaluationType,
            AddEvaluationType, SubEvaluationType, MulEvaluationType, DivEvaluationType, PowEvaluationType, ModulusEvaluationType,
            IdEvaluationType, FunctionEvaluationType,
            EqualsEvaluationType, IndexEvaluationType, FactorialEvaluationType
        ))

        fun addEvalType(type: EvaluationType) {
            types.add(type)
        }

        fun evaluateTree(tree: TreeNode): Any {
            for (type in types) {
                if (type.forType == tree.type || (type.aliases.contains(tree.type))) {
                    return type.evaluate(tree)
                }
            }

            throw IllegalStateException("Unknown tree type ${tree.type}")
        }
    }
}