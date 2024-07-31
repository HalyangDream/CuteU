package com.cute.logic.http.response.call

import com.google.gson.annotations.SerializedName

data class CallInfoResponse(
    @SerializedName("show_auto_end_time")
    val showAutoEndTime: Boolean,
    @SerializedName("auto_end_time")
    val autoEndTime: Int?,
    @SerializedName("strategy_messages")
    val strategyMessageList: MutableList<String>?
)
