package com.amigo.logic.http

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

object HttpCommonParam {


    interface HttpCommonParamConfig {

        fun getParam(): Map<String, Any>
    }

    private var config: HttpCommonParamConfig? = null

    fun setHttpCommonParamConfig(config: HttpCommonParamConfig?) {
        this.config = config
    }

    fun getCommonParam(): JSONObject {
        if (config != null) {
            val jsonObject = JSONObject()
            for (entry in config!!.getParam()) {
                jsonObject.put(entry.key, entry.value)
            }
            return jsonObject
        }
        return JSONObject()
    }

    fun JSONObject.toRequestBody(): RequestBody {
        return RequestBody.create(
            "application/json;charset=utf-8".toMediaTypeOrNull(),
            this.toString()
        )
    }
}