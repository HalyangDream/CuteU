package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.tool.Translate
import com.amigo.logic.http.service.ToolService
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