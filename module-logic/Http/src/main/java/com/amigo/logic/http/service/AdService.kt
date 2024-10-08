package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import com.amigo.logic.http.response.ad.AdConfig
import com.amigo.logic.http.response.ad.PlayAdScenesResult
import com.amigo.logic.http.response.ad.RewardVideoAdReward
import com.amigo.logic.http.response.ad.RewardVideoAdRewardInfo
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AdService {

    @POST("/v1/ad/config")
    suspend fun adConfig(@Body body: RequestBody): ApiResponse<AdConfig>

    @POST("/v1/ad/playScenes")
    suspend fun reportPlayAdScenes(@Body body: RequestBody): ApiResponse<PlayAdScenesResult>


    @POST("/v1/ad/rewardVideoAdRewardInfo")
    suspend fun getRewardVideoInfo(@Body body: RequestBody): ApiResponse<RewardVideoAdRewardInfo>


    @POST("/v1/ad/playRewardVideoComplete")
    suspend fun playRewardVideoComplete(@Body body: RequestBody): ApiResponse<RewardVideoAdReward>


}