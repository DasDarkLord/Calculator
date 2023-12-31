package evaluator

import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import parser.TreeNode
import java.lang.reflect.Field
import java.util.concurrent.CompletableFuture

class Evaluator {
    companion object {
        private val reflections = Reflections(ConfigurationBuilder()
            .forPackage("evaluator"))

        private val types = reflections.getSubTypesOf(EvaluationType::class.java).map { type ->
            val instanceField: Field?
            try{
                instanceField = type.getDeclaredField("INSTANCE")
            } catch (nsfe: NoSuchFieldException) {
                return@map null
            }
            return@map instanceField.get(null) as EvaluationType
        }.filterNotNull()

        fun evaluateTree(tree: TreeNode, constants: Map<List<String>, Any>? = null): Any {
            val uConsts = calcConstants.constants
            if (constants != null) {
                calcConstants.constants.putAll(constants)
            }

            for (type in types) {
                if (type.forType == tree.type || (type.aliases.contains(tree.type))) {
                    try {
                        return type.evaluate(tree, tree.type)
                    } finally {
                        if (constants != null) {
                            calcConstants.constants.clear()
                            calcConstants.constants.putAll(uConsts)
                        }
                    }
                }
            }

            if (constants != null) {
                calcConstants.constants.clear()
                calcConstants.constants.putAll(uConsts)
            }

            throw IllegalStateException("Unknown tree type ${tree.type}")
        }
    }
}