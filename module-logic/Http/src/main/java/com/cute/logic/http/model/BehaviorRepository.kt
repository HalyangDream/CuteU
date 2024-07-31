package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.service.BehaviorService

class BehaviorRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(BehaviorService::class.java) }


    suspend fun followerUser(userId: Long): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { service.followerUser(param.toRequestBody()) }
    }

    suspend fun unFollowerUser(userId: Long): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { service.unFollowerUser(param.toRequestBody()) }
    }

    suspend fun blockUser(id: Long): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", id)
        }
        return launchRequest { service.blockUser(param.toRequestBody()) }
    }

    suspend fun unBlockUser(userId: Long): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
        }
        return launchRequest { service.unBlockUser(param.toRequestBody()) }
    }

    suspend fun reportUser(userId: Long, reportId: String): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("user_id", userId)
            put("report_id", reportId)
        }
        return launchRequest { service.reportUser(param.toRequestBody()) }
    }
}