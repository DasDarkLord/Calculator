package parser

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import lexer.Token

data class TreeNode(val type: String, val left: TreeNode? = null, val right: TreeNode? = null, val value: Any? = null, val arguments: List<TreeNode>? = null) {
    private fun jsonObject(): JsonObject {
        val json = JsonObject()

        json.addProperty("type", type)
        if (value != null) {
            if (value is Number) json.addProperty("value", value.toDouble())
            else if (value is String) json.addProperty("value", value)
            else if (value is TreeNode) json.add("value", value.jsonObject())
            else json.addProperty("value", value.toString())
        } else {
            json.add("left", left!!.jsonObject())
            json.add("right", right!!.jsonObject())
        }
        if (arguments != null) {
            val argsArr = JsonArray()
            for (tree in arguments) argsArr.add(tree.jsonObject())
            json.add("arguments", argsArr)
        }

        return json
    }

    fun json(): String {
        return GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create()
            .toJson(jsonObject())
    }

}