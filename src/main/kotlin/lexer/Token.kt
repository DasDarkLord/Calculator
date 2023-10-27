package lexer

import com.google.gson.JsonObject

class Token(val type: TokenType, val value: Any, var unfinished: Boolean = false) {

    override fun toString(): String {
        val json = JsonObject()
        json.addProperty("type", type.id)
        if (value is Number) json.addProperty("value", value.toDouble())
        if (value is String) json.addProperty("value", value)
        return json.toString()
    }

}