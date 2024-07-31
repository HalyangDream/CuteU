package com.cute.login.state

import com.cute.basic.UserState
import com.cute.logic.http.response.account.AccountProfileInfo

sealed class LoginState : UserState {

    data class Logging(val isLogging: Boolean) : LoginState()

    data class SUCCESS(val response: AccountProfileInfo) : LoginState()

    data class ERROR(val error: String) : LoginState()

}