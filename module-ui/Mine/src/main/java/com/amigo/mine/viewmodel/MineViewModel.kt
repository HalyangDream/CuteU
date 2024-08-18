package com.amigo.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.logic.http.model.ProductRepository
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.logic.http.model.UserRepository
import com.amigo.mine.intent.MineIntent
import com.amigo.mine.state.MineState
import kotlinx.coroutines.launch

class MineViewModel : BaseMVIModel<MineIntent, MineState>() {

    private val profileRepository = ProfileRepository()
    private val productRepository = ProductRepository()

    override fun processIntent(intent: MineIntent) {
        when (intent) {
            is MineIntent.MeInfo -> getMeUserInfo()

            is MineIntent.VipPowerData -> getVipPowerData()
        }
    }


    private fun getMeUserInfo() {
        viewModelScope.launch {
            val response = profileRepository.getProfileInfo()
            if (response.isSuccess) {
                setState(MineState.MeUserState(response.data))
            }
        }
    }

    private fun getVipPowerData() {
        viewModelScope.launch {
            val response = productRepository.getVipPowerData()
            setState(MineState.VipPowerResult(response.data?.list))
        }
    }
}