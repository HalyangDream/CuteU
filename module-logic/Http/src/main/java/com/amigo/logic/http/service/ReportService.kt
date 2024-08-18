package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST


interface ReportService {

    @POST("v1/report/store_close")
    suspend fun reportStoreClose(@Body body: RequestBody): ApiResponse<Unit>

    @POST("v1/report/event")
    suspend fun reportEvent(@Body body: RequestBody): ApiResponse<Unit>
}