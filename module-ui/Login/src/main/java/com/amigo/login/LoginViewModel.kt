package com.amigo.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amigo.login.intent.LoginIntent
import com.amigo.login.state.LoginState
import com.amigo.basic.BaseMVIModel
import com.amigo.logic.http.model.AccountRepository
import com.amigo.logic.http.model.ConfigRepository
import com.amigo.tool.AppUtil
import kotlinx.coroutines.launch

class LoginViewModel : BaseMVIModel<LoginIntent, LoginState>() {

    private val _loginRepository by lazy { AccountRepository() }

    override fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.AccountLogin -> loginWithAccount(intent.userName, intent.password)
            is LoginIntent.VisitorLogin -> loginWithVisitor(intent.token)
        }
    }


    private fun loginWithVisitor(token: String) {
        viewModelScope.launch {
            setState(LoginState.Logging(true))
            val apiResponse = _loginRepository.deviceLogin(token)
            setState(LoginState.Logging(false))
            if (apiResponse.isSuccess) {
                setState(LoginState.SUCCESS(apiResponse.data!!))
            } else {
                setState(LoginState.ERROR(apiResponse.msg))
            }
        }
    }

    private fun loginWithAccount(userName: String, password: String) {
        viewModelScope.launch {
            setState(LoginState.Logging(true))
            val apiResponse = _loginRepository.accountLogin(userName, password)
            setState(LoginState.Logging(false))
            if (apiResponse.isSuccess) {
                setState(LoginState.SUCCESS(apiResponse.data!!))
            } else {
                setState(LoginState.ERROR(apiResponse.msg))
            }
        }
    }
}