package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

public class StringArgument extends Argument<String> {

    @Override
    public String accept(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        if (evaluated instanceof Boolean) return ((Boolean)evaluated).toString();
        return (String) evaluated;
    }

    @Override
    public boolean accepts(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        return evaluated instanceof String || evaluated instanceof Boolean;
    }

}