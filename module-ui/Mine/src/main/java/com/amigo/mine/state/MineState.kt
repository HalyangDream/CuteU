package com.amigo.mine.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.product.VipPowerInfoData
import com.amigo.logic.http.response.profile.Profile

sealed class MineState : UserState {

    data class MeUserState(val userInfo: Profile?) : MineState()

    data class VipPowerResult(val list: MutableList<VipPowerInfoData>?) : MineState()
}
