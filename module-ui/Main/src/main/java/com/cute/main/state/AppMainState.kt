package com.cute.main.state

import com.cute.basic.UserState
import com.cute.logic.http.response.product.PackageShow
import com.cute.logic.http.response.profile.Profile

sealed class AppMainState : UserState {
    
    data class UpdatePersonInfo(val info: Profile) : AppMainState()

    data class UnReadCount(val count: Int) : AppMainState()

    data class ProductHomeInfo(val info: PackageShow?) : AppMainState()

}
