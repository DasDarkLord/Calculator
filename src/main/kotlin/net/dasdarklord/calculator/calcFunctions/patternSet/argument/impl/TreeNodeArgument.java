package net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.parser.TreeNode;

public class TreeNodeArgument extends Argument<TreeNode> {

    @Override
    public TreeNode accept(TreeNode tree) {
        return tree;
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return true;
    }

}
