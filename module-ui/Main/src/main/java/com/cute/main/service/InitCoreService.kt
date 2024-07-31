package com.cute.main.service

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.cute.baselogic.userDataStore
import com.cute.im.IMCore
import com.cute.im.bean.User
import com.cute.im.listener.UserCacheConfig
import com.cute.im.service.UserService
import com.cute.logic.http.model.ConfigRepository
import com.cute.logic.http.model.UserRepository
import com.cute.rtc.RtcManager
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService

internal object InitCoreService {

    private val configRepository = ConfigRepository()
    private val _userRepository = UserRepository()


    suspend fun initCoreService(context: Context) {
        initServer(context)
        val iTelephoneService = RouteSdk.findService(ITelephoneService::class.java)
        iTelephoneService.tryResumeCall()
    }

    private suspend fun initServer(context: Context) {
        val isInit = RtcManager.getInstance().isInit
        if (!isInit) {
            val response = configRepository.getAgoraConfig()
            val agora = response.data?.agoraKey
            val rtmToken = response.data?.rtmToken
            if (!agora.isNullOrEmpty()) {
                RtcManager.getInstance().initRtc(context, agora, "3.7.0")
                IMCore.initSdk(context, agora)
                loginIM(context, rtmToken)
                initCallServer()
            }
        } else {
            val isLogin = IMCore.getService(UserService::class.java).isLogin()
            if (!isLogin) {
                val response = configRepository.getAgoraConfig()
                val rtmToken = response.data?.rtmToken
                loginIM(context, rtmToken)
            }
            val iTelephoneService = RouteSdk.findService(ITelephoneService::class.java)
            val isEnable = iTelephoneService.callServiceIsEnable()
            if (!isEnable) {
                initCallServer()
            }
        }
    }

    private fun initCallServer() {
        val iTelephoneService = RouteSdk.findService(ITelephoneService::class.java)
        iTelephoneService.init()
    }

    private suspend fun loginIM(context: Context, rtmToken: String?) {
        if (rtmToken.isNullOrEmpty()) return
        val uid = context.userDataStore.getUid()
        IMCore.getService(UserService::class.java).login("$uid", rtmToken)
        IMCore.getService(UserService::class.java).setupUserConfig(object : UserCacheConfig {
            override suspend fun updateCache(userId: String?): User? {
                if (userId.isNullOrEmpty() || !userId.isDigitsOnly()) return null
                val response = _userRepository.getUserDetail(userId.toLong())
                val anchor = response.data ?: return null
                return User(uid = "${anchor.id}", name = anchor.name, avatar = anchor.avatar)
            }

            override fun updateCacheTime(): Long {
                return 12 * 60 * 60
            }
        })
    }


}