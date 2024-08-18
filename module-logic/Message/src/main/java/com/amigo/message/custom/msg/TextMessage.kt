package com.amigo.message.custom.msg

import com.amigo.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class TextMessage : CustomMessage() {

    var messageContent: String? = null
    var messageTranslate: String? = null
    override fun toJson(): JSONObject? {
        val jsonObject = JSONObject()
        val textContentObj = JSONObject()
        textContentObj.put("content", messageContent)
        textContentObj.put("message_translate", messageTranslate)
        jsonObject.put("text_content", textContentObj)
        return jsonObject
    }

    override fun parseJson(json: String?) {
        if (json.isNullOrEmpty()) return
        try {
            val jsonObject = JSONObject(json)
            val textContentObj = jsonObject.optJSONObject("text_content")
            messageContent = textContentObj?.optString("content")
            messageTranslate = textContentObj?.optString("message_translate")
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun identity(): Int {
        return 1
    }

    override fun identityString(): String = "Text"

    override fun intoDb(): Boolean {
        return true
    }

    override fun shortContent(): String {
        return messageContent ?: ""
    }

}