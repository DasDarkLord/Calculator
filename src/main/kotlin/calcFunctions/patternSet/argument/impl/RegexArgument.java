package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import evaluator.Evaluator;
import kotlin.text.Regex;
import parser.TreeNode;

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
