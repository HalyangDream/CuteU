package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.message.ShowVipLock
import com.amigo.logic.http.service.MessageService

class MessageRepository : ApiRepository() {

    private val _service by lazy { ApiClient.getService(MessageService::class.java) }


    suspend fun sendMessage(payload: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("payload", payload)
        }
        return launchRequest { _service.sendMessage(param.toRequestBody()) }
    }

    suspend fun unlockBlurMessage(
        messageId: String,
        url: String,
        isVideo: Boolean
    ): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("message_id", messageId)
            put("is_video", isVideo)
            put("url", url)
        }
        return launchRequest { _service.unlockBlurMessage(param.toRequestBody()) }
    }

    suspend fun showVipLock(userId: Long): ApiResponse<ShowVipLock> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { _service.showVipLock(param.toRequestBody()) }

    }
}