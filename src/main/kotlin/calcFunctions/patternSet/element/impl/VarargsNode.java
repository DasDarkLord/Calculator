package calcFunctions.patternSet.element.impl;

import calcFunctions.patternSet.argument.Argument;
import calcFunctions.patternSet.argument.impl.TreeNodeArgument;
import calcFunctions.patternSet.element.ElementResult;
import calcFunctions.patternSet.element.PatternElement;
import calcFunctions.patternSet.exception.InvalidArgumentException;
import parser.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class VarargsNode implements PatternElement {

    public String nodeName;
    public Argument<?>[] allowedTypes;

    public Object[] defaultValues;
    public boolean optional;
    public boolean list;

    public boolean allows(TreeNode tree) {
        for (Argument<?> argument : allowedTypes) if (argument.accepts(tree)) return true;
        return false;
    }

    private Argument<?> sameArg(TreeNode tree) {
        for (Argument<?> a : allowedTypes) {
            if (a.accepts(tree)) return a;
        }

        return new TreeNodeArgument();
    }

    public VarargsNode(String name, Argument<?>... allowed) {
        nodeName = name;
        allowedTypes = allowed;
    }

    public VarargsNode setOptional(Object defaultValues) {
        this.defaultValues = new Object[] { defaultValues };
        optional = true;
        return this;
    }

    public VarargsNode setVarargsOptional(Object... defaultValues) {
        this.defaultValues = defaultValues;
        optional = true;
        return this;
    }

    @Override
    public ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException {
        List<Object> vs = new ArrayList<>();
        for (TreeNode node : list) {
            if (!allows(node)) break;
            Argument<?> argument = sameArg(node);
            vs.add(argument.accept(node));
        }

        if (vs.isEmpty() && optional) {
            vs = new ArrayList<>(List.of(defaultValues));
            return new ElementResult().addToArgumentMap(nodeName, vs);
        }

        return new ElementResult().addToArgumentMap(nodeName, vs).setItemsToRemove(vs.size());
    }
}
