package com.amigo.mine.intent

import com.amigo.basic.UserIntent

sealed class MineIntent : UserIntent {

    object MeInfo : MineIntent()

    object VipPowerData : MineIntent()

}
