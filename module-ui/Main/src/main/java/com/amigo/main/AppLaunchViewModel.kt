package com.amigo.main

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.logic.http.model.AccountRepository
import com.amigo.main.intent.LaunchIntent
import com.amigo.main.state.LaunchState
import kotlinx.coroutines.launch

class AppLaunchViewModel : BaseMVIModel<LaunchIntent, LaunchState>() {


    private val _loginRepository by lazy { AccountRepository() }

    override fun processIntent(intent: LaunchIntent) {
        when (intent) {
            is LaunchIntent.CheckToken -> updateToken()

            is LaunchIntent.LoginWithVisitor -> loginWithVisitor(intent.token)
        }
    }

    private fun loginWithVisitor(token: String) {
        viewModelScope.launch {
            val apiResponse = _loginRepository.deviceLogin(token)
            if (apiResponse.isSuccess) {
                setState(LaunchState.LoginSuccess(apiResponse.data!!))
            } else {
                setState(LaunchState.GoLogin)
            }
        }
    }


    private fun updateToken() {
        viewModelScope.launch {
            val response = _loginRepository.tokenAvailable()
            if (response.isSuccess) {
                setState(LaunchState.UpdateTokenSuccess(response.data?.token!!))
            } else {
                setState(LaunchState.GoLogin)
            }
        }
    }
}