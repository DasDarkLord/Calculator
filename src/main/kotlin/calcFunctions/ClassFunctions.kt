package calcFunctions

import calcFunctions.argumentSet.ArgumentSet
import calcFunctions.patternSet.PatternSet
import calcFunctions.patternSet.argument.impl.AnyArgument
import calcFunctions.patternSet.argument.impl.NumberArgument
import calcFunctions.patternSet.argument.impl.StringArgument
import calcFunctions.patternSet.element.impl.AlternateSetElement
import calcFunctions.patternSet.element.impl.OrderedSetElement
import calcFunctions.patternSet.element.impl.SingletonNode
import calcFunctions.patternSet.element.impl.StaticNode

val classFunctions = mapOf(
    listOf("add") to listOf(ListAddFunction, MapAddFunction)
)

interface ClassFunction<T> {
    val forClass: Class<*>
    val patternSet: PatternSet
    fun execute(affected: Any, argumentSet: ArgumentSet)
}

// Default Functions

object ListAddFunction : ClassFunction<MutableList<*>> {
    override val forClass: Class<*>
        get() = MutableList::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(AlternateSetElement(
                OrderedSetElement(
                    SingletonNode("index", NumberArgument()),
                    SingletonNode("value", AnyArgument())
                ),
                OrderedSetElement(
                    StaticNode("index", null),
                    SingletonNode("value", AnyArgument())
                )
            ))

    override fun execute(affected: Any, argumentSet: ArgumentSet) {
        affected is MutableList<*>

        if (argumentSet.hasValue("index")) (affected as MutableList<Any>).add(argumentSet.getValue<Number>("index").toInt(), argumentSet.getValue("value"))
        else (affected as MutableList<Any>).add(argumentSet.getValue("value"))
    }
}

object MapAddFunction : ClassFunction<MutableMap<*, *>> {
    override val forClass: Class<*>
        get() = MutableMap::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("key", StringArgument()))
            .addElement(SingletonNode("value", AnyArgument()))

    override fun execute(affected: Any, argumentSet: ArgumentSet) {
        affected is MutableMap<*, *>

        (affected as MutableMap<Any, Any>)[argumentSet.getValue<String>("key")] = argumentSet.getValue("value")
    }
}