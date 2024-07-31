package com.cute.main.notify

import com.google.gson.annotations.SerializedName

data class AppMessage(
    @SerializedName("notify_type") val notifyType: Int,
    val id: Long,
    val avatar: String,
    val title:String,
    val content:String
)
