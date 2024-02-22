package net.dasdarklord.calculator.evaluator

import net.dasdarklord.calculator.parser.TreeNode

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
            val uConsts = net.dasdarklord.calculator.calcConstants.constants
            if (constants != null) {
                net.dasdarklord.calculator.calcConstants.constants.putAll(constants)
            }

            for (type in types) {
                if (type.forType == tree.type || (type.aliases.contains(tree.type))) {
                    try {
                        return type.evaluate(tree, tree.type)
                    } finally {
                        if (constants != null) {
                            net.dasdarklord.calculator.calcConstants.constants.clear()
                            net.dasdarklord.calculator.calcConstants.constants.putAll(uConsts)
                        }
                    }
                }
            }

            if (constants != null) {
                net.dasdarklord.calculator.calcConstants.constants.clear()
                net.dasdarklord.calculator.calcConstants.constants.putAll(uConsts)
            }

            throw IllegalStateException("Unknown tree type ${tree.type}")
        }
    }
}