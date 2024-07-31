package com.cute.logic.http.response.profile

import com.google.gson.annotations.SerializedName

data class AccountInfo(
    val id: Long,
    @SerializedName("username") val userName: String,
    val password: String
)
