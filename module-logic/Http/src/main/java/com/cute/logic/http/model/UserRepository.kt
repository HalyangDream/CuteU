package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.user.ChatUserInfo
import com.cute.logic.http.response.user.UserDetail
import com.cute.logic.http.service.UserService

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