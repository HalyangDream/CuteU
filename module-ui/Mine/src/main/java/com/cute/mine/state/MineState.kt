package com.cute.mine.state

import com.cute.basic.UserState
import com.cute.logic.http.response.product.VipPowerInfoData
import com.cute.logic.http.response.profile.Profile

sealed class MineState : UserState {

    data class MeUserState(val userInfo: Profile?) : MineState()

    data class VipPowerResult(val list: MutableList<VipPowerInfoData>?) : MineState()
}
