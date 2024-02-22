package net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.evaluator.Evaluator;
import kotlin.text.Regex;
import net.dasdarklord.calculator.parser.TreeNode;

public class RegexArgument extends Argument<Regex> {

    @Override
    public Regex accept(TreeNode tree) {
        return (Regex) Evaluator.Companion.evaluateTree(tree, null);
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return Evaluator.Companion.evaluateTree(tree, null) instanceof Regex;
    }

}
