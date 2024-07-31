package com.cute.main.service

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.cute.baselogic.storage.UserDataStore
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.bean.User
import com.cute.im.service.ConversationService
import com.cute.im.service.UserService
import com.cute.main.notify.AppMessage
import com.cute.main.notify.AppMessageEnum
import com.cute.main.notify.AppMessageViewFactory
import com.cute.message.custom.msg.CallRecordMessage
import com.cute.tool.AppUtil
import com.cute.tool.NotificationUtils
import com.cute.uibase.ActivityStack
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal object ReceiveMsgService {

    private val telephoneService by lazy { RouteSdk.findService(ITelephoneService::class.java) }

    fun handleReceiveMsg(context: Context, scope: CoroutineScope, message: Msg) {
        if (telephoneService.isCalling()) return
        if (message.message is CallRecordMessage) return
        val peerId = IMCore.getService(ConversationService::class.java).getChattingId()
        val userId = UserDataStore.get(context).getUid().toString()
        if (peerId == message.sendId) return
        if (userId == message.sendId) return
        scope.launch(Dispatchers.IO) {
            val user = IMCore.getService(UserService::class.java).getUserInfo(message.sendId)
                ?: return@launch
            withContext(Dispatchers.Main) {
                if (ActivityStack.isBackground()) {
                    sendNotification(context, user, message)
                } else {
                    sendAppInternalNotification(user, message)
                }
            }
        }
    }


    private fun sendNotification(context: Context, user: User, message: Msg) {
        if (!NotificationUtils.checkPermission(context)) return
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationUtils.createChannel(
                "message", "RemoteMessage", NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationUtils.createNotificationChannel(context, channel)
            NotificationCompat.Builder(context, channel.id)
        } else {
            NotificationCompat.Builder(context, "message")
        }


        val person = Person.Builder()
            .setName(if (user.name.isNullOrEmpty()) AppUtil.getApplicationName(context) else user.name)
            .setKey(message.sendId)
            .setImportant(true)
            .build()
        val resId = context.resources.getIdentifier("ic_launcher", "mipmap", context.packageName)
        builder.setSmallIcon(resId)
        builder.setStyle(
            NotificationCompat.MessagingStyle(person)
                .addMessage(message.message?.shortContent() ?: "", message.timeStamp, person)
        )
        builder.setContentTitle(if (user.name.isNullOrEmpty()) AppUtil.getApplicationName(context) else user.name)
        builder.setContentText(message.message?.shortContent() ?: "")
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        NotificationUtils.sendNotification(context, message.sendId.toInt(), builder.build())
    }


    private fun sendAppInternalNotification(user: User, message: Msg) {
        AppMessageViewFactory.consume(
            message = AppMessage(
                AppMessageEnum.MESSAGE.value,
                message.sendId.toLong(),
                user.avatar!!,
                user.name,
                message.message?.shortContent() ?: ""
            )
        )
    }

}