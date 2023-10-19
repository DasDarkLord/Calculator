package calcFunctions.patternSet.element.impl;

import calcFunctions.patternSet.element.ElementResult;
import calcFunctions.patternSet.element.PatternElement;
import calcFunctions.patternSet.exception.InvalidArgumentException;
import com.sun.source.tree.Tree;
import parser.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderedSetElement implements PatternElement {
    public PatternElement[] elements;

    public OrderedSetElement(PatternElement... elements) {
        this.elements = elements;
    }

    @Override
    public ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException {
        ElementResult result = new ElementResult();

        int items = 0;
        for (PatternElement element : elements) {
            List<TreeNode> i = new ArrayList<>(list.subList(items, list.size()));

            ElementResult otherResult = element.takeItems(i);
            for (Map.Entry<String, List<Object>> entry : otherResult.getArgumentMap().entrySet()) result.addToArgumentMap(entry.getKey(), entry.getValue());
            items += otherResult.getItemsToRemove();
        }

        return result.setItemsToRemove(items);
    }
}
