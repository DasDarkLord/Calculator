package calcFunctions.patternSet.element.impl;

import calcFunctions.patternSet.element.ElementResult;
import calcFunctions.patternSet.element.PatternElement;
import calcFunctions.patternSet.exception.InvalidArgumentException;
import parser.TreeNode;

import java.util.List;

public class AlternateSetElement implements PatternElement {

    private PatternElement right;
    private PatternElement left;


    public AlternateSetElement(PatternElement left, PatternElement right) {
       this.left = left;
       this.right = right;
    }

    @Override
    public ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException {
        try {
            return left.takeItems(list);
        } catch (InvalidArgumentException ignored) {
            return right.takeItems(list);
        }
    }
}
