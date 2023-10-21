package calcFunctions.patternSet.argument;

import calcFunctions.patternSet.argument.impl.*;
import parser.TreeNode;

import java.util.ArrayList;
import java.util.List;

public abstract class Argument<T> {
    static List<Argument<?>> args = new ArrayList<>();

    static {
        add(new NumberArgument());
        add(new StringArgument());
        add(new BooleanArgument());
        add(new RegexArgument());
        add(new ListArgument());
        add(new TreeNodeArgument());
        add(new AnyArgument());
    }

    public abstract T accept(TreeNode tree);
    public abstract boolean accepts(TreeNode tree);

    public static void add(Argument<?> arg) {
        args.add(arg);
    }

    public static Argument<?> argOf(TreeNode tree) {
        for (Argument<?> arg : args) {
            if (arg.accepts(tree)) return arg;
        }

        return new TreeNodeArgument();
    }
}
