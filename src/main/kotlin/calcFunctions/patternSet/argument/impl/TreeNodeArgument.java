package calcFunctions.patternSet.argument.impl;

import calcFunctions.patternSet.argument.Argument;
import parser.TreeNode;

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
