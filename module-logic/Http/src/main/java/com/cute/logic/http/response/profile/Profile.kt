package com.cute.logic.http.response.profile

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("user_id")
    val id: Long,
    @SerializedName("nick_name")
    val name: String,
    val avatar: String,
    val gender: Int,
    val age: Int,
    val role: String,
    val balance: String,
    @SerializedName("is_vip")
    val isVip: Boolean,
    @SerializedName("vip_expired_time")
    val vipExpiredTime: String,
    @SerializedName("is_coin_mode")
    val isCoinMode: Boolean,
    @SerializedName("free_match_num")
    val freeMatchNum: String?
)