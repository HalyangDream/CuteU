package com.amigo.message.custom.msg

import com.amigo.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class CallRecordMessage : CustomMessage() {

    var duration: Long = 0
    var message_content: String? = null

    override fun toJson(): JSONObject? {
        val json = JSONObject()
        json.put("audio_duration", duration)
        json.put("message_content", message_content)
        return json
    }

    override fun parseJson(json: String?) {
        try {
            val jsonObj = JSONObject(json)
            duration = jsonObj.optLong("audio_duration", 0)
            message_content = jsonObj.optString("message_content", "")
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    /**
     * 30001-39999
     * 因为是纯本地消息，所以自己定一个范围
     */
    override fun identity(): Int {
        return 30001
    }

    override fun intoDb(): Boolean {
        return true
    }

    override fun shortContent(): String {
        return "[ Call ]"
    }

    override fun identityString(): String {
        return "CallRecord"
    }

}