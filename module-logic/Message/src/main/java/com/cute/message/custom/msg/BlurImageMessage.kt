package com.cute.message.custom.msg

import com.cute.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class BlurImageMessage : CustomMessage() {
    /**
     * 网络地址
     */
    var url: String? = null
    var isBlur: Boolean = true

    override fun toJson(): JSONObject? {
        val json = JSONObject()
        val imgObj = JSONObject()
        imgObj.put("url", url)
        imgObj.put("is_blur", isBlur)
        json.put("media_content", imgObj)
        return json
    }

    override fun parseJson(json: String?) {
        try {
            val jsonObj = JSONObject(json)
            val imgContentObj = jsonObj.optJSONObject("media_content")
            url = imgContentObj?.optString("url", "")
            isBlur = imgContentObj?.optBoolean("is_blur", true) ?: true
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun identity(): Int {
        return 4
    }

    override fun identityString(): String = "Image"
    override fun intoDb(): Boolean {
        return true
    }


    override fun shortContent(): String {
        return "[ Image ]"
    }
}