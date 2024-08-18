package com.amigo.main.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.product.PackageShow
import com.amigo.logic.http.response.profile.Profile

sealed class AppMainState : UserState {
    
    data class UpdatePersonInfo(val info: Profile) : AppMainState()

    data class UnReadCount(val count: Int) : AppMainState()

    data class ProductHomeInfo(val info: PackageShow?) : AppMainState()

}
