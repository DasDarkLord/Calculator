package net.dasdarklord.calculator.calcFunctions.patternSet.element.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.TreeNodeArgument;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.ElementResult;
import net.dasdarklord.calculator.calcFunctions.patternSet.element.PatternElement;
import net.dasdarklord.calculator.calcFunctions.patternSet.exception.InvalidArgumentException;
import net.dasdarklord.calculator.parser.TreeNode;

import java.util.List;

public class SingletonNode implements PatternElement {

    public String nodeName;
    public Argument<?>[] allowedTypes;

    public Object defaultValue;
    public boolean optional;

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

    public SingletonNode(String name, Argument<?>... allowed) {
        nodeName = name;
        allowedTypes = allowed;
    }

    public SingletonNode setOptional(Object defaultValue) {
        this.defaultValue = defaultValue;
        optional = true;
        return this;
    }

    @Override
    public ElementResult takeItems(List<TreeNode> list) throws InvalidArgumentException {
        if (list.isEmpty()) {
            if (optional) {
                return new ElementResult().addToArgumentMap(nodeName, defaultValue);
            } else throw new InvalidArgumentException("Empty list");
        }
        TreeNode node = list.get(0);

        if (!allows(node)) {
            if (optional) {
                return new ElementResult().addToArgumentMap(nodeName, defaultValue);
            } else throw new InvalidArgumentException("Provided incorrect argument type");
        }

        Argument<?> argument = sameArg(node);
        return new ElementResult().addToArgumentMap(nodeName, argument.accept(node)).setItemsToRemove(1);
    }

}
