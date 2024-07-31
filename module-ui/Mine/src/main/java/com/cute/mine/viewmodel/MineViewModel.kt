package com.cute.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.model.ProfileRepository
import com.cute.logic.http.model.UserRepository
import com.cute.mine.intent.MineIntent
import com.cute.mine.state.MineState
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