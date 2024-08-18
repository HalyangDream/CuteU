package com.amigo.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.im.IMCore
import com.amigo.im.service.ConversationService
import com.amigo.logic.http.model.ConfigRepository
import com.amigo.logic.http.model.ProductRepository
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.main.intent.AppMainIntent
import com.amigo.main.state.AppMainState
import kotlinx.coroutines.launch

class AppMainViewModel : BaseMVIModel<AppMainIntent, AppMainState>() {


    private val _profileRepository = ProfileRepository()
    private val _productRepository = ProductRepository()
    private val _configRepository = ConfigRepository()
    override fun processIntent(intent: AppMainIntent) {
        when (intent) {

            is AppMainIntent.InitService -> initService()

            is AppMainIntent.GetUnReadCount -> getMsgUnreadCount(intent.uid)

            is AppMainIntent.NewUserProductInfo -> getProductHomeShow()
        }
    }


    private fun initService() {
        viewModelScope.launch {
            val mineResponse = _profileRepository.getProfileInfo().data
            if (mineResponse != null) {
                setState(AppMainState.UpdatePersonInfo(mineResponse))
            }
        }
    }


    private fun getMsgUnreadCount(uid: Long) {
        viewModelScope.launch {
            val count = IMCore.getService(ConversationService::class.java).getUnReadCount("$uid")
            setState(AppMainState.UnReadCount(count))
        }
    }

    private fun getProductHomeShow() {
        viewModelScope.launch {
            val response = _productRepository.getPackageShow()
            setState(AppMainState.ProductHomeInfo(response.data))
        }
    }
}