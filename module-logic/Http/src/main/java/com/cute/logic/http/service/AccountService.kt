package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.account.AccountProfileInfo
import com.cute.logic.http.response.account.AccountToken
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountService {

    @POST("/v1/account/token_available")
    suspend fun tokenAvailable(@Body requestBody: RequestBody): ApiResponse<AccountToken>

    @POST("/v1/account/device_login")
    suspend fun deviceLogin(@Body requestBody: RequestBody): ApiResponse<AccountProfileInfo>

    @POST("/v1/account/account_login")
    suspend fun accountLogin(@Body requestBody: RequestBody): ApiResponse<AccountProfileInfo>


}