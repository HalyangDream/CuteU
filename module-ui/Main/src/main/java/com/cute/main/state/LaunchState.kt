package com.cute.main.state

import com.cute.basic.UserState
import com.cute.logic.http.response.account.AccountProfileInfo

sealed class LaunchState : UserState {


    data class UpdateTokenSuccess(val token: String) : LaunchState()

    data class LoginSuccess(val data: AccountProfileInfo) : LaunchState()
    object GoLogin : LaunchState()

}
