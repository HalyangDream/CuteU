package com.amigo.tool

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * author : mac
 * date   : 2022/2/17
 * e-mail : taolei@51cashbox.com
 */
object NotificationUtils {


    /**
     * 是否有通知栏权限
     */
    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        } else {
            val managerCompat = NotificationManagerCompat.from(context)
            managerCompat.areNotificationsEnabled()
        }
    }

    /**
     * 跳转到开启通知栏权限的界面
     */
    fun openNotificationSettingLayout(context: Activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            context.requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0x1617
            )
        } else {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo?.uid)
                } else {
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo?.uid)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    }

    /**
     * Android8.0及以上会将通知栏进行分组
     * channel 相当于分组的组名
     **/
    fun createNotificationChannel(context: Context, vararg channels: NotificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Register the channel with the system
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.createNotificationChannels(channels.toMutableList())
        }
    }

    /**
     * Android8.0及以上
     * 创建NotificationChannel
     * @param channelId 分组ID
     * @param channelName 分组名
     * @param importance 通知等级
     * @param sound 收到通知时的音效
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(
        channelId: String,
        channelName: String,
        importance: Int,
        sound: Uri? = null
    ): NotificationChannel {

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.setShowBadge(true)
        if (sound != null) {
            val audioAttributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            channel.setSound(sound, audioAttributes)
        }

        return channel
    }

    /**
     * 发送通知
     * @param id 通知栏的ID，最好有唯一性，可以通过这个取消发送的通知
     * @param notification 发送通知栏
     */
    fun sendNotification(context: Context, id: Int?, notification: Notification?) {
        try {
            if (id != null && notification != null) {
                NotificationManagerCompat.from(context).notify(id, notification)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 移除掉已经发送的通知
     * @param id 通知栏ID
     */
    fun removeNotification(context: Context, id: Int?) {
        try {
            if (id != null) {
                NotificationManagerCompat.from(context).cancel(id)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}