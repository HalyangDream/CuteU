package com.cute.uibase.event

sealed class GetRewardEvent {

    object VipAdReward : GetRewardEvent()

    object CoinAdReward : GetRewardEvent()

    object DayReward : GetRewardEvent()

}