package com.cute.logic.http.response.config

import com.google.gson.annotations.SerializedName

data class AgoraConfig(
    @SerializedName("agora_key") val agoraKey: String,
    @SerializedName("rtm_token") val rtmToken: String
)
