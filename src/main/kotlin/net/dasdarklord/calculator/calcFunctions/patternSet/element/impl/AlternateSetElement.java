package net.dasdarklord.calculator.calcFunctions.patternSet.element.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.element.ElementResult;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.PatternElement;
import net.dasdarklord.calculator.calcFunctions.patternSet.exception.InvalidArgumentException;
import net.dasdarklord.calculator.parser.TreeNode;

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
