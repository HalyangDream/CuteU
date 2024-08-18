package com.amigo.main.service

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.im.IMCore
import com.amigo.im.bean.Msg
import com.amigo.im.bean.User
import com.amigo.im.service.ConversationService
import com.amigo.im.service.UserService
import com.amigo.main.notify.AppMessage
import com.amigo.main.notify.AppMessageEnum
import com.amigo.main.notify.AppMessageViewFactory
import com.amigo.message.custom.msg.CallRecordMessage
import com.amigo.tool.AppUtil
import com.amigo.tool.NotificationUtils
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
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