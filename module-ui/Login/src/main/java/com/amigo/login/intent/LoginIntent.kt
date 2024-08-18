package com.amigo.login.intent

import com.amigo.basic.UserIntent

sealed class LoginIntent : UserIntent {

    data class AccountLogin(val userName: String, val password: String) : LoginIntent()

    data class VisitorLogin(val token: String) : LoginIntent()
}