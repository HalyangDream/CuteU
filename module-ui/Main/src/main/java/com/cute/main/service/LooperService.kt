package com.cute.main.service

import android.content.Context
import com.cute.baselogic.storage.UserDataStore
import com.cute.baselogic.userDataStore
import com.cute.logic.http.model.ConfigRepository
import com.cute.main.notify.AppMessage
import com.cute.main.notify.AppMessageViewFactory
import com.cute.tool.EventBus
import com.cute.uibase.ActivityStack
import com.cute.uibase.event.FollowerUnReadEvent
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService
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