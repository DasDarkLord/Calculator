package calcFunctions.patternSet;

import calcFunctions.patternSet.element.PatternElement;

import java.util.ArrayList;
import java.util.List;

public class PatternSet {

    private final List<PatternElement> elements = new ArrayList<>();

    public PatternSet addElement(PatternElement element) {
        elements.add(element);
        return this;
    }

    public PatternElement[] getElements() {
        return elements.toArray(new PatternElement[0]);
    }

}
