package net.dasdarklord.calculator.calcFunctions.patternSet.element.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.element.ElementResult;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.PatternElement;
import net.dasdarklord.calculator.calcFunctions.patternSet.exception.InvalidArgumentException;
import net.dasdarklord.calculator.parser.TreeNode;

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
