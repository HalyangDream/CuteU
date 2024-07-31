package com.cute.login.intent

import com.cute.basic.UserIntent

sealed class LoginIntent : UserIntent {

    data class AccountLogin(val userName: String, val password: String) : LoginIntent()

    data class VisitorLogin(val token: String) : LoginIntent()
}