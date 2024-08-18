package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import com.amigo.logic.http.response.user.ChatUserInfo
import com.amigo.logic.http.response.user.UserDetail
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("/v1/user/user_info")
    suspend fun getUserDetail(@Body body: RequestBody): ApiResponse<UserDetail>

    @POST("/v1/user/chat_user_info")
    suspend fun getChatUserInfo(@Body body: RequestBody): ApiResponse<ChatUserInfo>

}