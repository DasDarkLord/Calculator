package net.dasdarklord.calculator.calcFunctions.patternSet.element;

import net.dasdarklord.calculator.calcFunctions.patternSet.exception.InvalidArgumentException;
import net.dasdarklord.calculator.parser.TreeNode;

import java.util.List;

public interface PatternElement {

    ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException;

}
