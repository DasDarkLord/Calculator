package net.dasdarklord.calculator.calcFunctions.argumentSet;

import net.dasdarklord.calculator.calcFunctions.patternSet.PatternSet;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.ElementResult;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.PatternElement;
import net.dasdarklord.calculator.calcFunctions.patternSet.exception.InvalidArgumentException;
import net.dasdarklord.calculator.parser.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PatternSetReader {

    private final HashMap<String, List<Object>> argumentMap;
    private final PatternSet patternSet;
    private boolean readSuccess;

    public PatternSetReader(PatternSet patternSet) {
        argumentMap = new HashMap<>();
        this.patternSet = patternSet;
    }

    public void readObjects(List<TreeNode> objects) {
        this.slotObjects = objects.toArray(new TreeNode[0]);
        patternElements = patternSet.getElements();

        int least = 0;

        while (getCurrentPatternElement() != null) {
            PatternElement element = getCurrentPatternElement();

            ElementResult result;
            try {
                result = element.takeItems(rightObjects());
                if (result == null) throw new InvalidArgumentException("Null Result");

                least++;
            } catch (InvalidArgumentException exc) {
                nextElement();
                exc.printStackTrace();
                continue;
            }
            argumentMap.putAll(result.getArgumentMap());
            slotObjectPos += result.getItemsToRemove();

            nextElement();
        }

        readSuccess = least >= patternElements.length;
    }

    private PatternElement[] patternElements;
    private TreeNode[] slotObjects;

    private int elementPos;
    private  int slotObjectPos;

    private List<TreeNode> rightObjects() {
        if (slotObjectPos > slotObjects.length - 1) return new ArrayList<>();
        return Arrays.stream(slotObjects).toList().subList(slotObjectPos, slotObjects.length);
    }

    private PatternElement getCurrentPatternElement() {
        if (elementPos >= patternElements.length) return null;
        return patternElements[elementPos];
    }

    private void nextElement() {
        elementPos++;
    }

    public ArgumentSet getSet() {
        return new ArgumentSet(argumentMap).withReadSuccess(readSuccess);
    }

}
