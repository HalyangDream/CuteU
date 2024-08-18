package com.amigo.main.service

import android.content.Context
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.baselogic.userDataStore
import com.amigo.logic.http.model.ConfigRepository
import com.amigo.main.notify.AppMessage
import com.amigo.main.notify.AppMessageViewFactory
import com.amigo.tool.EventBus
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.event.FollowerUnReadEvent
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object LooperService {

    private val configRepository = ConfigRepository()
    private val iTelephoneService by lazy(LazyThreadSafetyMode.NONE) {
        RouteSdk.findService(
            ITelephoneService::class.java
        )
    }

    private var heartJob: Job? = null


    fun init(scope: CoroutineScope) {

    }

    fun heart(scope: CoroutineScope, context: Context) {
        heartJob = scope.launch {
            while (true) {
                if (context.userDataStore.readToken().isNotEmpty()) {
                    val isBackground = ActivityStack.isBackground()
                    val isCall = iTelephoneService.isCalling()
                    val response = configRepository.heart(isBackground, isCall)
                    val followNotify = response.data?.followNotify
                    if (followNotify != null) {
                        val unReadCount = followNotify.unreadNum ?: 0
                        UserDataStore.get(context).saveLikeMeUnReadCount(unReadCount)
                        EventBus.post(FollowerUnReadEvent(unReadCount))
                    }
                }
                delay(5 * 1000)
            }
        }
    }

    fun stopHeart() {
        heartJob?.cancel()
    }


    /**
     * 发送APP内部通知
     */
    private fun sendAppInternalNotification(message: AppMessage) {
        if (!ActivityStack.isBackground()) {
            AppMessageViewFactory.consume(message)
        }
    }

}