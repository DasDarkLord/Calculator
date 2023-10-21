package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import parser.TreeNode;

import java.util.List;

public class ListArgument extends Argument<List<?>> {
    @Override
    public List<?> accept(TreeNode tree) {
        return (List<?>) Evaluator.Companion.evaluateTree(tree, null);
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree, null) instanceof List<?>;
    }
}
