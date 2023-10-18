package calcFunctions.patternSet.element;

import calcFunctions.patternSet.exception.InvalidArgumentException;
import parser.TreeNode;

import java.util.List;

public interface PatternElement {

    ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException;

}
