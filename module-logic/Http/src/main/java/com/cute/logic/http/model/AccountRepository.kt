package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.Gender
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.account.AccountProfileInfo
import com.cute.logic.http.response.account.AccountToken
import com.cute.logic.http.service.AccountService

class AccountRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(AccountService::class.java) }


    suspend fun deviceLogin(thirdToken: String): ApiResponse<AccountProfileInfo> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("third_token", thirdToken)
        }
        return launchRequest { service.deviceLogin(param.toRequestBody()) }
    }

    suspend fun accountLogin(
        userName: String, password: String
    ): ApiResponse<AccountProfileInfo> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("username", userName)
            put("password", password)
        }
        return launchRequest { service.accountLogin(param.toRequestBody()) }
    }

    suspend fun tokenAvailable(): ApiResponse<AccountToken> {
        val param = HttpCommonParam.getCommonParam()
        return launchRequest { service.tokenAvailable(param.toRequestBody()) }
    }
}