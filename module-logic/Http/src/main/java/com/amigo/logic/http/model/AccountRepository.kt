package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.Gender
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.account.AccountProfileInfo
import com.amigo.logic.http.response.account.AccountToken
import com.amigo.logic.http.service.AccountService

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