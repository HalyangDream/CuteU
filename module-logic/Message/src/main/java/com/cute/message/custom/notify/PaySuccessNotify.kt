package com.cute.message.custom.notify

import com.cute.im.cutom.CustomNotify
import org.json.JSONException
import org.json.JSONObject

class PaySuccessNotify : CustomNotify() {

    var orderNo: String? = null
        private set

    override fun parseJson(json: String?) {
        if (json.isNullOrEmpty()) return
        try {
            val jsonObject = JSONObject(json)
            val payNotifyObj = jsonObject.optJSONObject("pay_notify")
            if (payNotifyObj != null) {
                orderNo = payNotifyObj.optString("order_no")
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

    }

    override fun notifyType(): Int = 2
}