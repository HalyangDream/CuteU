package com.amigo.logic.http.response.ad

import com.google.gson.annotations.SerializedName

data class RewardVideoAdReward(
    @SerializedName("is_give_reward")
    val isGiveReward: Boolean,
    @SerializedName("reward_content")
    val rewardContent: String?,
    @SerializedName("reward_type")
    val rewardType: String

)
