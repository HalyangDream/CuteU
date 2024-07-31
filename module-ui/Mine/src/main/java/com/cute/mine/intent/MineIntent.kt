package com.cute.mine.intent

import com.cute.basic.UserIntent

sealed class MineIntent : UserIntent {

    object MeInfo : MineIntent()

    object VipPowerData : MineIntent()

}
