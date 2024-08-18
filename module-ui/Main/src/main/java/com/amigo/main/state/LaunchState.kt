package com.amigo.main.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.account.AccountProfileInfo

sealed class LaunchState : UserState {


    data class UpdateTokenSuccess(val token: String) : LaunchState()

    data class LoginSuccess(val data: AccountProfileInfo) : LaunchState()
    object GoLogin : LaunchState()

}
