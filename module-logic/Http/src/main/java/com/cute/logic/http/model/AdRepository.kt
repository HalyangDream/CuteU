package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.ad.AdConfig
import com.cute.logic.http.response.ad.PlayAdScenesResult
import com.cute.logic.http.response.ad.RewardVideoAdReward
import com.cute.logic.http.response.ad.RewardVideoAdRewardInfo
import com.cute.logic.http.service.AdService


class AdRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(AdService::class.java) }

    suspend fun getAdConfig(): ApiResponse<AdConfig> {
        return launchRequest {
            service.adConfig(HttpCommonParam.getCommonParam().toRequestBody())
        }
    }

    suspend fun reportPlayAdScenes(scenes: String, code: String?): ApiResponse<PlayAdScenesResult> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("source", scenes)
            if (!code.isNullOrEmpty()) {
                put("code", code)
            }
        }
        return launchRequest {
            service.reportPlayAdScenes(param.toRequestBody())
        }
    }

    suspend fun getRewardVideoInfo(type: String): ApiResponse<RewardVideoAdRewardInfo> {
        val params = HttpCommonParam.getCommonParam().apply {
            this.put("type", type)
        }
        return launchRequest {
            service.getRewardVideoInfo(params.toRequestBody())
        }
    }

    suspend fun playRewardVideoComplete(type: String): ApiResponse<RewardVideoAdReward> {
        val params = HttpCommonParam.getCommonParam().apply {
            this.put("type", type)
        }
        return launchRequest {
            service.playRewardVideoComplete(params.toRequestBody())
        }
    }
}