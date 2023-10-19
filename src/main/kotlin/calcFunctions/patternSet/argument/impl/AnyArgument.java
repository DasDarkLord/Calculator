package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import evaluator.Undefined;
import parser.TreeNode;

public class AnyArgument extends Argument<Object> {

    @Override
    public Object accept(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        if (evaluated instanceof Undefined) return 0.0;
        return evaluated;
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return true;
    }

    public static AnyArgument INSTANCE = new AnyArgument();

}
