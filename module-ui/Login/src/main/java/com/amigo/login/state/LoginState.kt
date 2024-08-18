package com.amigo.login.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.account.AccountProfileInfo

sealed class LoginState : UserState {

    data class Logging(val isLogging: Boolean) : LoginState()

    data class SUCCESS(val response: AccountProfileInfo) : LoginState()

    data class ERROR(val error: String) : LoginState()

}