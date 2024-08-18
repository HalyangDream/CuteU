package com.amigo.message.custom.notify

import com.amigo.im.cutom.CustomNotify
import org.json.JSONException
import org.json.JSONObject

class PopCodeNotify : CustomNotify() {

    var popCode: String? = null
        private set

    override fun parseJson(json: String?) {
        if (json.isNullOrEmpty()) return
        try {
            val jsonObject = JSONObject(json)
            val popNotifyObj = jsonObject.optJSONObject("pop_notify")
            if (popNotifyObj != null) {
                popCode = popNotifyObj.optString("pop_code")
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

    }

    override fun notifyType(): Int = 4
}