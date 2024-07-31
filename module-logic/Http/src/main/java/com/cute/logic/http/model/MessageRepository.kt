package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.service.MessageService

class MessageRepository : ApiRepository() {

    private val _service by lazy { ApiClient.getService(MessageService::class.java) }


    suspend fun sendMessage(payload: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("payload", payload)
        }
        return launchRequest { _service.sendMessage(param.toRequestBody()) }
    }

    suspend fun unlockBlurMessage(messageId: String, url:String,isVideo: Boolean): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("message_id", messageId)
            put("is_video", isVideo)
            put("url", url)
        }
        return launchRequest { _service.unlockBlurMessage(param.toRequestBody()) }
    }

}