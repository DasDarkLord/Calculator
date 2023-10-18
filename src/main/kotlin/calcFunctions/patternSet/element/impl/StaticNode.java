package calcFunctions.patternSet.element.impl;

import calcFunctions.patternSet.element.ElementResult;
import calcFunctions.patternSet.element.PatternElement;
import calcFunctions.patternSet.exception.InvalidArgumentException;
import parser.TreeNode;

import java.util.List;

public class StaticNode implements PatternElement {

    public String nodeName;
    public Object value;

    public StaticNode(String name, Object value) {
        nodeName = name;
        this.value = value;
    }

    @Override
    public ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException {
        ElementResult result = new ElementResult();
        result.addToArgumentMap(nodeName, value);

        return result;
    }

}
