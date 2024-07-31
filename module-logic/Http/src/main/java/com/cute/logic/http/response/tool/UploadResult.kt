package com.cute.logic.http.response.tool

sealed class UploadResult {

    data class Success(val fileUrl: String, val fileKey: String? = null) : UploadResult()

    data class Failure(val error: String) : UploadResult()
}
