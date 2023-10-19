package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

public class BooleanArgument extends Argument<Boolean> {

    @Override
    public Boolean accept(TreeNode tree) {
        return (Boolean) Evaluator.Companion.evaluateTree(tree, null);
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree, null) instanceof Boolean;
    }

}
