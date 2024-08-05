package com.cute.main

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.cute.analysis.Analysis
import com.cute.baselogic.statusDataStore
import com.cute.baselogic.userDataStore
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomNotify
import com.cute.im.listener.IMMessageListener
import com.cute.im.listener.IMNotifyListener
import com.cute.im.listener.IMStatusListener
import com.cute.im.service.MsgServiceObserver
import com.cute.im.service.UserService
import com.cute.logic.http.model.CallRepository
import com.cute.logic.http.model.ProductRepository
import com.cute.main.service.InitCoreService
import com.cute.main.service.ReceiveMsgService
import com.cute.main.service.LooperService
import com.cute.message.custom.notify.PaySuccessNotify
import com.cute.message.custom.notify.PopCodeNotify
import com.cute.message.custom.notify.RatingDialogNotify
import com.cute.message.custom.notify.StrategyCallNotify
import com.cute.pay.GooglePayClient
import com.cute.rtc.RtcManager
import com.cute.tool.AppUtil
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.NotificationUtils
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.PayResultEvent
import com.cute.uibase.event.RemoteNotifyEvent
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IMineService
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.route.provider.ITelephoneService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class CoreService private constructor() : ActivityStack.AppStateListener(), IMStatusListener,
    IMMessageListener, IMNotifyListener {

    private var contextRef: WeakReference<Context>? = null
    private val isCreate = AtomicBoolean(false)

    init {
        ActivityStack.addAppStateListener(this)
    }

    companion object {
        @Volatile
        private var instance: CoreService? = null

        fun getInstance(): CoreService {
            return instance ?: synchronized(this) {
                instance ?: CoreService().also { instance = it }
            }
        }
    }

    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _callRepository = CallRepository()
    private val iTelephoneService by lazy(LazyThreadSafetyMode.NONE) {
        RouteSdk.findService(
            ITelephoneService::class.java
        )
    }

    private val iStoreService by lazy(LazyThreadSafetyMode.NONE) {
        RouteSdk.findService(
            IStoreService::class.java
        )
    }

    private val iMineService by lazy(LazyThreadSafetyMode.NONE) {
        RouteSdk.findService(
            IMineService::class.java
        )
    }

    private fun mContext() = contextRef?.get()

    fun onCreate(context: Context) {
        contextRef = WeakReference(context)
        if (isCreate.compareAndSet(false, true)) {
            LooperService.init(serviceScope)
            IMCore.getService(MsgServiceObserver::class.java).observerReceiveNotify(this, true)
            IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, true)
            IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, true)
            contextRef?.get()?.let {
                GooglePayClient.initialize(it) {}
                AdPlayService.cacheAd(it)
            }
            EventBus.event.subscribe<RemoteNotifyEvent>(serviceScope) {
                if (it is RemoteNotifyEvent.PaySuccessEvent) {
                    getDeviceFunctionInfo()
                    fixGoogleOrder()
                    contextRef?.get()?.let {
                        Toaster.showShort(it, "Pay Success")
                    }

                }
            }
            EventBus.event.subscribe<PayResultEvent>(serviceScope) {
                fixGoogleOrder()
            }
        }

    }

    override fun onAppFront() {
        super.onAppFront()
//        startForegroundNotification()
        fixGoogleOrder()
        getDeviceFunctionInfo()
        serviceScope.launch {
            val context = mContext()
            context?.let {
                InitCoreService.initCoreService(it)
                LooperService.stopHeart()
                LooperService.heart(serviceScope, it)
            }
        }
    }

    override fun onAppBackground() {
        super.onAppBackground()
    }


//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        return START_STICKY
//    }


//    override fun onDestroy() {
//        super.onDestroy()
//        IMCore.getService(MsgServiceObserver::class.java).observerReceiveNotify(this, false)
//        IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, false)
//        IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, false)
//        RtcManager.getInstance().destroy()
//        serviceScope.cancel("Service Destroy")
//    }


//    private fun startForegroundNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            startForegroundNotificationMoreV31()
//        } else {
//            startForegroundNotificationLessV31()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun startForegroundNotificationMoreV31() {
//        try {
//            if (Build.VERSION.SDK_INT >= 34) {
//                startForeground(
//                    10, getNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
//                )
//            } else {
//                startForeground(10, getNotification())
//            }
//        } catch (ex: ForegroundServiceStartNotAllowedException) {
//            ex.printStackTrace()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }
//
//    private fun startForegroundNotificationLessV31() {
//        try {
//            startForeground(10, getNotification())
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }


    //================== IM Start==================

    override fun reLogin() {
        IMCore.getService(UserService::class.java).logout()
        serviceScope.launch {
            contextRef?.get()?.let { InitCoreService.initCoreService(it) }
        }
    }

    override fun loginSuccess() {

    }

    override fun renewToken() {
        IMCore.getService(UserService::class.java).logout()
        serviceScope.launch {
            contextRef?.get()?.let { InitCoreService.initCoreService(it) }
        }
    }

    override fun kickOut() {
        IMCore.getService(UserService::class.java).logout()
    }

    override fun onServerBanned() {
    }

    override fun onReceiveNotify(notify: CustomNotify) {
        when (notify) {
            is PaySuccessNotify -> EventBus.post(RemoteNotifyEvent.PaySuccessEvent)

            is StrategyCallNotify -> {
                val strategyCallNotify = notify as StrategyCallNotify
                val uid = contextRef?.get()?.userDataStore?.getUid()
                if (!strategyCallNotify.remoteId.isNullOrEmpty() && uid != null) {
                    iTelephoneService.launchStrategyCall(
                        uid,
                        strategyCallNotify.remoteId!!.toLong(),
                        strategyCallNotify.isFreeCall,
                        strategyCallNotify.triggerSource ?: "income_call",
                    )
                }
            }

            is RatingDialogNotify -> {
                if (ActivityStack.isBackground()) return
                val topActivity = ActivityStack.getTopActivity() ?: return
                if (iTelephoneService.isCalling()) return
                iMineService.showRatingDialog(topActivity)
            }

            is PopCodeNotify -> {
                if (ActivityStack.isBackground()) return
                if (notify.popCode.isNullOrEmpty()) return
                iStoreService.showCodeDialog(notify.popCode!!, null)
            }
        }
    }

    override fun onReceiveMsg(message: Msg) {
        serviceScope.launch(Dispatchers.Main) {
            if (mContext() == null) return@launch
            ReceiveMsgService.handleReceiveMsg(mContext()!!, serviceScope, message)
        }
    }
    //================== IM End==================

    private fun fixGoogleOrder() {
        if (mContext() == null) return
        RouteSdk.findService(IStoreService::class.java).fixGoogleOrder(mContext()!!)
    }

    private fun getDeviceFunctionInfo() {
        serviceScope.launch {
            val response = _callRepository.deviceFunctionUnlockInfo()
            if (response.isSuccess) {
                val list = response.data?.list ?: return@launch
                val context = mContext() ?: return@launch
                for (deviceFunctionInfo in list) {
                    if (!deviceFunctionInfo.enable) {
                        when (deviceFunctionInfo.type) {
                            "camera_close" -> context.statusDataStore.saveCloseCamera(false)
                            "camera_switch" -> context.statusDataStore.saveUseFrontCamera(
                                true
                            )

                            "voice_mute" -> context.statusDataStore.saveMuteVoice(false)
                        }
                    }
                }
            }
        }
    }
}