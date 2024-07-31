package com.cute.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.im.IMCore
import com.cute.im.service.ConversationService
import com.cute.logic.http.model.ConfigRepository
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.model.ProfileRepository
import com.cute.main.intent.AppMainIntent
import com.cute.main.state.AppMainState
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