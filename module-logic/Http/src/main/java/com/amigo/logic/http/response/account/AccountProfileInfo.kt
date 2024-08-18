package com.amigo.logic.http.response.account

import com.google.gson.annotations.SerializedName

data class AccountProfileInfo(
    @SerializedName("user_id")
    val id: Long,
    @SerializedName("nick_name")
    val name: String,
    val avatar: String,
    val token: String,
    @SerializedName("login_type")
    val loginType: Int,
    val role: String
)
