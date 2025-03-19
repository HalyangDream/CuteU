package com.amigo.message.custom.notify

import com.amigo.im.cutom.CustomNotify
import org.json.JSONException
import org.json.JSONObject

class PaySuccessNotify : CustomNotify() {

    var orderNo: String? = null
        private set
    var productName: String? = null
        private set
    var price: Double? = null
        private set
    var google: String? = null
        private set


    override fun parseJson(json: String?) {
        if (json.isNullOrEmpty()) return
        try {
            val jsonObject = JSONObject(json)
            val payNotifyObj = jsonObject.optJSONObject("pay_notify")
            if (payNotifyObj != null) {
                orderNo = payNotifyObj.optString("order_no")
                productName = payNotifyObj.optString("product_name")
                price = payNotifyObj.optDouble("price")
                google = payNotifyObj.optString("google")

            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

    }

    override fun notifyType(): Int = 2
}