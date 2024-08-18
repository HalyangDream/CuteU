package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.user.ChatUserInfo
import com.amigo.logic.http.response.user.UserDetail
import com.amigo.logic.http.service.UserService

class UserRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(UserService::class.java) }

    suspend fun getUserDetail(
        userId: Long
    ): ApiResponse<UserDetail> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { service.getUserDetail(params.toRequestBody()) }
    }

    suspend fun getChatUserInfo(
        userId: Long
    ): ApiResponse<ChatUserInfo> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { service.getChatUserInfo(params.toRequestBody()) }
    }

}