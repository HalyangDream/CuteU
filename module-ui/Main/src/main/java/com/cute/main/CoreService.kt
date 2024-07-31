package com.cute.main

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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

class CoreService : Service(), IMStatusListener, IMMessageListener, IMNotifyListener {


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


    override fun onCreate() {
        super.onCreate()
        LooperService.init(serviceScope)
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveNotify(this, true)
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, true)
        IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, true)
        GooglePayClient.initialize(this) {}
        AdPlayService.cacheAd(this)
        EventBus.event.subscribe<RemoteNotifyEvent>(serviceScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                getDeviceFunctionInfo()
                fixGoogleOrder()
                Toaster.showShort(this, "Pay Success")
            }
        }
        EventBus.event.subscribe<PayResultEvent>(serviceScope) {
            fixGoogleOrder()
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundNotification()
        fixGoogleOrder()
        getDeviceFunctionInfo()
        serviceScope.launch {
            InitCoreService.initCoreService(this@CoreService)
            LooperService.stopHeart()
            LooperService.heart(serviceScope, this@CoreService)
        }
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveNotify(this, false)
        IMCore.getService(MsgServiceObserver::class.java).observerReceiveMessage(this, false)
        IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, false)
        RtcManager.getInstance().destroy()
        serviceScope.cancel("Service Destroy")
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startForegroundNotificationMoreV31()
        } else {
            startForegroundNotificationLessV31()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startForegroundNotificationMoreV31() {
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(
                    10, getNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
                )
            } else {
                startForeground(10, getNotification())
            }
        } catch (ex: ForegroundServiceStartNotAllowedException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun startForegroundNotificationLessV31() {
        try {
            startForeground(10, getNotification())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    //================== IM Start==================

    override fun reLogin() {
        IMCore.getService(UserService::class.java).logout()
        serviceScope.launch {
            InitCoreService.initCoreService(this@CoreService)
        }
    }

    override fun loginSuccess() {

    }

    override fun renewToken() {
        IMCore.getService(UserService::class.java).logout()
        serviceScope.launch {
            InitCoreService.initCoreService(this@CoreService)
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
                if (!strategyCallNotify.remoteId.isNullOrEmpty()) {
                    iTelephoneService.launchStrategyCall(
                        userDataStore.getUid(),
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
            ReceiveMsgService.handleReceiveMsg(this@CoreService, serviceScope, message)
        }
    }
    //================== IM End==================

    private fun getNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationUtils.createChannel(
                "message", "RemoteMessage", NotificationManager.IMPORTANCE_MIN
            )
            NotificationUtils.createNotificationChannel(this, channel)
            NotificationCompat.Builder(this, channel.id)
        } else {
            NotificationCompat.Builder(this, "message")
        }
        builder.setSmallIcon(this.resources.getIdentifier("ic_launcher","mipmap",packageName))
        builder.setContentTitle(AppUtil.getApplicationName(this))
        builder.setContentText("Running")
        builder.setContentIntent(getPendingIntent())
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        return builder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(
            this,
            0x1111,
            intent,
            if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun fixGoogleOrder() {
        RouteSdk.findService(IStoreService::class.java).fixGoogleOrder(this)
    }

    private fun getDeviceFunctionInfo() {
        serviceScope.launch {
            val response = _callRepository.deviceFunctionUnlockInfo()
            if (response.isSuccess) {
                val list = response.data?.list ?: return@launch
                for (deviceFunctionInfo in list) {
                    if (!deviceFunctionInfo.enable) {
                        when (deviceFunctionInfo.type) {
                            "camera_close" -> this@CoreService.statusDataStore.saveCloseCamera(false)
                            "camera_switch" -> this@CoreService.statusDataStore.saveUseFrontCamera(
                                true
                            )

                            "voice_mute" -> this@CoreService.statusDataStore.saveMuteVoice(false)
                        }
                    }
                }
            }
        }
    }
}