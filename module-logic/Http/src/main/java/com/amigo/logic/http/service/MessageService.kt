package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import com.amigo.logic.http.response.message.ShowVipLock
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageService {
    @POST("/v1/message/send")
    suspend fun sendMessage(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/message/unlock_blur_msg")
    suspend fun unlockBlurMessage(@Body body: RequestBody): ApiResponse<Unit>

    @POST("/v1/message/config")
    suspend fun showVipLock(@Body body: RequestBody): ApiResponse<ShowVipLock>

}