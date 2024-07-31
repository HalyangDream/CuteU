package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.config.AgoraConfig
import com.cute.logic.http.response.config.GlobalConfig
import com.cute.logic.http.response.config.HeartResponse
import com.cute.logic.http.response.config.OfficialAccount
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ConfigService {

    @POST("/v1/config/heart")
    suspend fun heart(@Body body: RequestBody): ApiResponse<HeartResponse>

    @POST("/v1/config/agora")
    suspend fun agoraConfig(@Body body: RequestBody): ApiResponse<AgoraConfig>

    @POST("/v1/config/officialAccount")
    suspend fun officialAccount(@Body body: RequestBody): ApiResponse<OfficialAccount>

}