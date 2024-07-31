package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.tool.Translate
import com.cute.logic.http.response.tool.UploadConfig
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Url

interface ToolService {


    @POST("/v1/tool/translate")
    suspend fun translate(@Body body: RequestBody): ApiResponse<Translate>

    @POST("/v1/tool/upload_config")
    suspend fun getUploadConfig(
        @Body body: RequestBody
    ): ApiResponse<UploadConfig>


    @PUT
    suspend fun uploadFile(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @Body body: RequestBody
    ): Response<Unit>
}