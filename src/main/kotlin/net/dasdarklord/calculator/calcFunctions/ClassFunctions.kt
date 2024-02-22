package net.dasdarklord.calculator.calcFunctions

import net.dasdarklord.calculator.calcFunctions.argumentSet.ArgumentSet
import net.dasdarklord.calculator.calcFunctions.patternSet.PatternSet
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.AnyArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.ListArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.NumberArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.RegexArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.argument.impl.StringArgument
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.AlternateSetElement
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.OrderedSetElement
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.SingletonNode
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.StaticNode
import net.dasdarklord.calculator.calcFunctions.patternSet.element.impl.VarargsNode
import net.dasdarklord.calculator.evaluator.Undefined
import net.dasdarklord.calculator.prettierVersion

val classFunctions = mapOf(
    listOf("add") to listOf(ListAddFunction, MapAddFunction),
    listOf("insert") to listOf(ListInsertFunction),
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
            .addElement(VarargsNode("values", AnyArgument(false)))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as MutableList<*>

        for (value in argumentSet.getVarargValue<Any>("values")) {
            @Suppress("UNCHECKED_CAST")
            (affected as MutableList<Any>).add(value)
        }

        return affected
    }
}

object ListInsertFunction : ClassFunction<MutableList<*>> {
    override val forClass: Class<*>
        get() = MutableList::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(SingletonNode("index", NumberArgument()))
            .addElement(VarargsNode("values", AnyArgument(false)))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        affected as MutableList<*>

        @Suppress("UNCHECKED_CAST")
        (affected as MutableList<Any>).addAll(0, argumentSet.getVarargValue("values"))

        return affected
    }
}

object ListConcatFunction : ClassFunction<MutableList<*>> {
    override val forClass: Class<*>
        get() = MutableList::class.java
    override val patternSet: PatternSet
        get() = PatternSet()
            .addElement(VarargsNode("lists", ListArgument()))

    override fun execute(affected: Any, argumentSet: ArgumentSet): Any {
        @Suppress("UNCHECKED_CAST")
        affected as MutableList<Any>

        println(argumentSet)
        for (value in argumentSet.getVarargValue<Any>("lists")) {
            @Suppress("UNCHECKED_CAST")
            value as MutableList<Any>

            affected.addAll(value)
        }

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

        @Suppress("UNCHECKED_CAST")
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