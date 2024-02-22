package net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.evaluator.Evaluator;
import net.dasdarklord.calculator.evaluator.Undefined;
import net.dasdarklord.calculator.parser.TreeNode;

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