package com.amigo.message.custom.notify

import com.amigo.im.cutom.CustomNotify
import org.json.JSONException
import org.json.JSONObject

class StrategyCallNotify : CustomNotify() {

    var remoteId: String? = null
        private set
    var triggerSource: String? = null
        private set
    var isFreeCall = false

    override fun parseJson(json: String?) {
        if (json.isNullOrEmpty()) return
        try {
            val jsonObject = JSONObject(json)
            val strategyCallNotify = jsonObject.optJSONObject("strategy_call_notify")
            if (strategyCallNotify != null) {
                remoteId = strategyCallNotify.optString("remote_id")
                triggerSource = strategyCallNotify.optString("trigger_source")
                isFreeCall = strategyCallNotify.optBoolean("is_free_call")
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
    }

    override fun notifyType(): Int = 1
}