package net.dasdarklord.calculator.parser

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.dasdarklord.calculator.lexer.Token

data class TreeNode(val type: String, val left: TreeNode? = null, val right: TreeNode? = null, val value: Any? = null, val arguments: List<TreeNode>? = null) {
    private fun jsonObject(): JsonObject {
        val json = JsonObject()

        json.addProperty("type", type)
        if (value != null) {
            if (value is Number) json.addProperty("value", value.toDouble())
            else if (value is String) json.addProperty("value", value)
            else if (value is TreeNode) json.add("value", value.jsonObject())
            else json.addProperty("value", value.toString())
        }
        if (left != null) json.add("left", left.jsonObject())
        if (right != null) json.add("right", right.jsonObject())
        if (arguments != null) {
            val argsArr = JsonArray()
            for (tree in arguments) argsArr.add(tree.jsonObject())
            json.add("arguments", argsArr)
        }

        return json
    }

    private fun gson(): Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()

    fun json(): String {
        return gson().toJson(jsonObject())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeNode

        if (type != other.type) return false
        if (left != other.left) return false
        if (right != other.right) return false
        if (value != other.value) return false
        if (arguments != other.arguments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (left?.hashCode() ?: 0)
        result = 31 * result + (right?.hashCode() ?: 0)
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (arguments?.hashCode() ?: 0)
        return result
    }

}