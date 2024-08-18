package com.amigo.logic.http.response.ad

import com.google.gson.annotations.SerializedName

data class AdConfig(
    @SerializedName("rv_ids") val rewardAdIds: MutableList<String>?,
    @SerializedName("int_ids") val interstitialAdIds: MutableList<String>?,
    @SerializedName("splash_ids") val splashIds: MutableList<String>?
)
