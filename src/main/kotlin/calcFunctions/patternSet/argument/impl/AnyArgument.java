package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

public class AnyArgument extends Argument<Object> {

    @Override
    public Object accept(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree);
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return true;
    }

    public static AnyArgument INSTANCE = new AnyArgument();

}
