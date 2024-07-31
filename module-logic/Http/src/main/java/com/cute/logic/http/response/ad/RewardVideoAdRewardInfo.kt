package com.cute.logic.http.response.ad

import com.google.gson.annotations.SerializedName

data class RewardVideoAdRewardInfo(
    val tip: String,
    @SerializedName("reward_img") val rewardImg: String,
    val reward: String,
    val progress: Int,
    @SerializedName("total_progress")
    val totalProgress: Int,
    @SerializedName("is_play_ad")
    val isPlayAd: Boolean,
    @SerializedName("toast_message")
    val toastMessage: String?
)
