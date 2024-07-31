package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.tool.Translate
import com.cute.logic.http.service.ToolService
import java.util.Locale

class ToolRepository : ApiRepository() {

    private val _service by lazy { ApiClient.getService(ToolService::class.java) }


    suspend fun translate(content: String, targetLanguage: Locale): ApiResponse<Translate> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("content", content)
            put("target_language", targetLanguage.language)
        }
        return launchRequest { _service.translate(param.toRequestBody()) }
    }
}