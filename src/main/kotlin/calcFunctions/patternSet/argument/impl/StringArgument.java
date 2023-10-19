package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

public class StringArgument extends Argument<String> {

    @Override
    public String accept(TreeNode tree) {
        return (String) Evaluator.Companion.evaluateTree(tree, null);
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree, null) instanceof String;
    }

}