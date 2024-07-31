package com.cute.message.custom.msg

import com.cute.im.cutom.CustomMessage
import org.json.JSONException
import org.json.JSONObject

class VideoMessage : CustomMessage() {

    /**
     * 网络地址
     */
    var cover: String? = null
    var url: String? = null

    override fun toJson(): JSONObject? {
        val json = JSONObject()
        val videoObj = JSONObject()
        videoObj.put("cover", cover)
        videoObj.put("url", url)
        json.put("media_content", videoObj)
        return json
    }

    override fun parseJson(json: String?) {
        try {
            val jsonObj = JSONObject(json)
            val videoContentObj = jsonObj.optJSONObject("media_content")
            cover = videoContentObj?.optString("cover")
            url = videoContentObj?.optString("url")
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun identity(): Int {
        return 3
    }

    override fun identityString(): String = "Video"

    override fun intoDb(): Boolean {
        return true
    }

    override fun shortContent(): String {
        return "[ Video ]"
    }
}