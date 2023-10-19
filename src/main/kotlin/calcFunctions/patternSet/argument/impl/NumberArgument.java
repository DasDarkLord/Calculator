package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import evaluator.Undefined;
import parser.TreeNode;

public class NumberArgument extends Argument<Double> {

    @Override
    public Double accept(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        if (evaluated instanceof Undefined) return 0.0;
        if (evaluated instanceof Boolean) return ((Boolean) evaluated) ? 1.0 : 0.0;
        return ((Number)evaluated).doubleValue();
    }

    @Override
    public boolean accepts(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        return evaluated instanceof Number || evaluated instanceof Boolean || evaluated instanceof Undefined;
    }
}