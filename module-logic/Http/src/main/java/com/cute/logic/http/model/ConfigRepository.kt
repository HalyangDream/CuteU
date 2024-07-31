package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.config.AgoraConfig
import com.cute.logic.http.response.config.GlobalConfig
import com.cute.logic.http.response.config.HeartResponse
import com.cute.logic.http.response.config.OfficialAccount
import com.cute.logic.http.service.ConfigService

class ConfigRepository : ApiRepository() {

    private val _service by lazy { ApiClient.getService(ConfigService::class.java) }

    suspend fun heart(isBackground: Boolean, isCall: Boolean): ApiResponse<HeartResponse> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("is_background", isBackground)
            put("is_calling", isCall)
        }
        return launchRequest { _service.heart(param.toRequestBody()) }
    }

    suspend fun getAgoraConfig(): ApiResponse<AgoraConfig> {
        val param = HttpCommonParam.getCommonParam()
        return launchRequest { _service.agoraConfig(param.toRequestBody()) }
    }


    suspend fun getOfficialAccount(): ApiResponse<OfficialAccount> {
        val param = HttpCommonParam.getCommonParam()
        return launchRequest { _service.officialAccount(param.toRequestBody()) }
    }
}