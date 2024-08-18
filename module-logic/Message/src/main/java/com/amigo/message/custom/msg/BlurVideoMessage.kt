package com.amigo.message.custom.msg

import com.amigo.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class BlurVideoMessage : CustomMessage() {

    /**
     * 网络地址
     */
    var cover: String? = null
    var url: String? = null
    var isBlur: Boolean = true

    override fun toJson(): JSONObject? {
        val json = JSONObject()
        val videoObj = JSONObject()
        videoObj.put("cover", cover)
        videoObj.put("url", url)
        videoObj.put("is_blur", isBlur)
        json.put("media_content", videoObj)
        return json
    }

    override fun parseJson(json: String?) {
        try {
            val jsonObj = JSONObject(json)
            val videoContentObj = jsonObj.optJSONObject("media_content")
            cover = videoContentObj?.optString("cover")
            url = videoContentObj?.optString("url")
            isBlur = videoContentObj?.optBoolean("is_blur", true) ?: true
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun identity(): Int {
        return 5
    }

    override fun identityString(): String = "Video"

    override fun intoDb(): Boolean {
        return true
    }

    override fun shortContent(): String {
        return "[ Video ]"
    }
}