package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

public class NumberArgument extends Argument<Double> {

    @Override
    public Double accept(TreeNode tree) {
        return ((Number)Evaluator.Companion.evaluateTree(tree)).doubleValue();
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree) instanceof Number;
    }
}