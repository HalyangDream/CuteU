package com.cute.message.custom.msg

import com.cute.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class ImageMessage : CustomMessage() {
    /**
     * 网络地址
     */
    var url: String? = null

    override fun toJson(): JSONObject? {
        val json = JSONObject()
        val imgObj = JSONObject()
        imgObj.put("url", url)
        json.put("media_content", imgObj)
        return json
    }

    override fun parseJson(json: String?) {
        try {
            val jsonObj = JSONObject(json)
            val imgContentObj = jsonObj.optJSONObject("media_content")
            url = imgContentObj?.optString("url", "")
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun identity(): Int {
        return 2
    }

    override fun identityString(): String = "Image"
    override fun intoDb(): Boolean {
        return true
    }


    override fun shortContent(): String {
        return "[ Image ]"
    }
}