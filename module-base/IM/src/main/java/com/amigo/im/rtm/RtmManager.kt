package com.amigo.im.rtm

import android.content.Context
import android.util.Log
import io.agora.rtm.*

internal class RtmManager private constructor() {

    private var mRtmClient: RtmClient? = null
    private var rtmListener: RtmListener? = null
    private val options by lazy {
        val sendMessageOptions = SendMessageOptions()
//        sendMessageOptions.enableOfflineMessaging = true
//        sendMessageOptions.enableHistoricalMessaging = false
        sendMessageOptions
    }

    @Volatile
    var isInitialize = false
        private set(value) {
            field = value
        }

    companion object {
        private const val TAG = "[ RtmManager ]"
        private val ins by lazy { RtmManager() }
        fun getInstance(): RtmManager {
            return ins
        }
    }

    fun getCallManager(): RtmCallManager? {
        return mRtmClient?.rtmCallManager
    }

    /**
     * 初始化Rtm
     */
    fun initialize(context: Context, appId: String) {
        if (isInitialize) {
            return
        }
        val init = try {
            rtmListener = RtmListener()
            mRtmClient = RtmClient.createInstance(context, appId, rtmListener)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
        isInitialize = init && mRtmClient != null
    }

    fun addEventListener(listener: IRtmListener?) {
        rtmListener?.addRtmListener(listener)
    }

    fun removeEventListener(listener: IRtmListener?) {
        rtmListener?.removeRtmListener(listener)
    }


    /**
     * 登录RTM
     */
    fun loginRtm(token: String, userId: String, block: ((Int) -> Unit)? = null) {
        if (!isInitialize) {
            "loginRtm RTM not initialized".logE()
            block?.invoke(-1)
            return
        }

        mRtmClient?.login(token, userId, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                block?.invoke(0)
            }

            override fun onFailure(p0: ErrorInfo?) {
                "loginRtm failed :${p0?.errorCode},${p0?.errorDescription}".logE()
                val result =
                    p0?.errorCode == io.agora.rtm.RtmStatusCode.LoginError.LOGIN_ERR_ALREADY_LOGIN
                block?.invoke(if (result) 0 else p0?.errorCode ?: -1)
            }
        })
    }

    /**
     * 登出RTM
     */
    fun logoutRtm(block: ((Int) -> Unit)? = null) {
        if (!isInitialize) {
            "logoutRtm RTM not initialized".logE()
            block?.invoke(-1)
            return
        }
        mRtmClient?.logout(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                block?.invoke(0)
            }

            override fun onFailure(p0: ErrorInfo?) {
                "logoutRtm failed :${p0?.errorCode},${p0?.errorDescription}".logE()
                block?.invoke(p0?.errorCode ?: -1)
            }
        })
    }

    /**
     * 创建一个频道
     */
    fun getChannel(channel: String, listener: RtmChannelListener): RtmChannel? {
        return mRtmClient?.createChannel(channel, listener)
    }

    /**
     * 释放RTM
     */
    fun destroy() {
        if (!isInitialize) {
            "destory RTM not initialized".logE()
            return
        }
        isInitialize = false
        mRtmClient?.release()
        mRtmClient = null
    }

    /**
     * 创建RTMMessage
     */
    fun createRtmMessage(): RtmMessage? {
        val message = mRtmClient?.createMessage()
        return message
    }

    /**
     * 创建RTMMessage
     */
    fun createRtmMessage(text: String): RtmMessage? {
        val message = mRtmClient?.createMessage()
        message?.text = text
        return null
    }

    /**
     * 发送消息
     */
    fun sendRtmMessage(channel: String, message: RtmMessage?, block: ((Int) -> Unit)? = null) {
        mRtmClient?.sendMessageToPeer(channel, message, options, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                block?.invoke(0)
            }

            override fun onFailure(p0: ErrorInfo?) {
                "send sendRtmMessage failed :${p0?.errorCode},${p0?.errorDescription}".logE()
                block?.invoke(p0?.errorCode ?: -1)
            }
        })
    }

    private fun String.logE() {
        Log.e(TAG, this)
    }

}