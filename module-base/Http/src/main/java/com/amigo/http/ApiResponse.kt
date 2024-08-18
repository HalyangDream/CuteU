package com.amigo.http

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("error_code")
    val code: String,
    @SerializedName("error_message")
    val msg: String,
    @SerializedName("success")
    val isSuccess: Boolean,
    val isHasException: Boolean = false,
    val data: T?
)
