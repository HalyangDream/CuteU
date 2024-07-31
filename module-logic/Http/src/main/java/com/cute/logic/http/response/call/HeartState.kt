package com.cute.logic.http.response.call

import com.google.gson.annotations.SerializedName

data class HeartState(
    @SerializedName("call_id") val callId: String,
    @SerializedName("is_continue") val isContinue: Boolean
)


