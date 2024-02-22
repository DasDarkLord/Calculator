package net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.evaluator.Evaluator;
import net.dasdarklord.calculator.parser.TreeNode;

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
