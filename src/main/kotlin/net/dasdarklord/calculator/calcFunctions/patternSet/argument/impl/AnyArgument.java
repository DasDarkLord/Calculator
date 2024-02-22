package net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl;

import net.dasdarklord.calculator.calcFunctions.patternSet.argument.Argument;
import net.dasdarklord.calculator.evaluator.Evaluator;
import net.dasdarklord.calculator.evaluator.Undefined;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import net.dasdarklord.calculator.parser.TreeNode;

public final class AnyArgument extends Argument<Object> {

    private boolean undefinedZero = true;

    public AnyArgument() {
    }

    public AnyArgument(boolean undefinedZero) {
        this.undefinedZero = undefinedZero;
    }

    @Override
    public Object accept(TreeNode tree) {
        Object evaluated = Evaluator.Companion.evaluateTree(tree, null);
        if (undefinedZero && evaluated instanceof Undefined) return 0.0;
        return evaluated;
    }

    @Override
    public boolean accepts(TreeNode tree) {
        return true;
    }

}
