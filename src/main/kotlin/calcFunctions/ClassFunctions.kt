package calcFunctions

import calcFunctions.argumentSet.ArgumentSet
import calcFunctions.patternSet.PatternSet
import calcFunctions.patternSet.argument.impl.AnyArgument
import calcFunctions.patternSet.argument.impl.ListArgument
import calcFunctions.patternSet.argument.impl.NumberArgument
import calcFunctions.patternSet.argument.impl.RegexArgument
import calcFunctions.patternSet.argument.impl.StringArgument
import calcFunctions.patternSet.element.impl.AlternateSetElement
import calcFunctions.patternSet.element.impl.OrderedSetElement
import calcFunctions.patternSet.element.impl.SingletonNode
import calcFunctions.patternSet.element.impl.StaticNode
import evaluator.Undefined
import prettierVersion

val classFunctions = mapOf(
    listOf("add") to listOf(ListAddFunction, MapAddFunction),
    listOf("concat") to listOf(ListConcatFunction),
    listOf("put") to listOf(MapAddFunction),
    listOf("replace") to listOf(ReplaceFunction)
)

interface ClassFunction<T> {
    val forClass: Class<*>
    val patternSet: PatternSet
    fun execute(affected: Any, argumentSet: ArgumentSet): Any
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
                    SingletonNode("value", AnyArgument(false))
                ),
                OrderedSetElement(
                    StaticNode("index", null),
                    SingletonNode("value", AnyArgument(false))
                )
            ))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as MutableList<*>

        if (argumentSet.hasValue("index")) (affected as MutableList<Any>).add(argumentSet.getValue<Number>("index").toInt(), argumentSet.getValue("value"))
        else (affected as MutableList<Any>).add(argumentSet.getValue("value"))

        return affected
    }
}

object ListConcatFunction : ClassFunction<MutableList<*>> {
    override val forClass: Class<*>
        get() = MutableList::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("list", ListArgument()))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as MutableList<Any>

        affected.addAll(argumentSet.getValue("list"))

        return affected
    }

}

object MapAddFunction : ClassFunction<MutableMap<*, *>> {
    override val forClass: Class<*>
        get() = MutableMap::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("key", StringArgument()))
            .addElement(SingletonNode("value", AnyArgument(false)))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as MutableMap<*, *>

        (affected as MutableMap<Any, Any>)[argumentSet.getValue<String>("key")] = argumentSet.getValue("value")

        return affected
    }
}

object ReplaceFunction : ClassFunction<String> {
    override val forClass: Class<*>
        get() = String::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("replace", StringArgument(), RegexArgument()))
            .addElement(SingletonNode("value", AnyArgument(false)))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as String

        val replace = argumentSet.getValue<Any>("replace")

        val with = prettierVersion(argumentSet.getValue("value"))

        val newValue = if (replace is Regex) {
            affected.replace(replace, with)
        } else affected.replace(replace as String, with)

        return newValue
    }

}