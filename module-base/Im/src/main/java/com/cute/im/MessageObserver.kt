package com.cute.im

import android.util.Log
import com.cute.im.bean.Conversation
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomNotify
import com.cute.im.listener.ConversationListener
import com.cute.im.listener.IMMessageListener
import com.cute.im.listener.IMNotifyListener
import com.cute.im.listener.IMStatusListener
import com.cute.im.listener.MsgSendResultListener
import com.cute.im.rtm.IMEventListener
import com.cute.im.rtm.RtmManager
import com.cute.im.service.ConversationService
import com.cute.im.service.MessageService
import io.agora.rtm.RtmMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * author : mac
 * date   : 2022/5/12
 *
 */
internal object MessageObserver {

    fun release() {
        notifyListeners.clear()
        messageListeners.clear()
        sendResultListener.clear()
        conversationListeners.clear()
        statusListeners.clear()
        scope.cancel()
    }

    private val notifyListeners by lazy { mutableListOf<WeakReference<IMNotifyListener>>() }
    private val messageListeners by lazy { mutableListOf<WeakReference<IMMessageListener>>() }
    private val sendResultListener by lazy { mutableListOf<WeakReference<MsgSendResultListener>>() }
    private val conversationListeners by lazy { mutableListOf<WeakReference<ConversationListener>>() }
    private val statusListeners by lazy { mutableListOf<WeakReference<IMStatusListener>>() }

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private val imEventListener by lazy {
        object : IMEventListener() {

            override fun onMustLogin() {
                notifyReLoginListener()
            }

            override fun onKickOut() {
                notifyKickOutListener()
            }

            override fun onTokenExpired() {
                notifyRenewTokenListener()
            }

            override fun onReceiveMessage(message: RtmMessage, channel: String?) {
                try {
                    val json = JSONObject(message.text)
                    val type = json.optString("type")
                    when (type.lowercase()) {
                        "notify" -> {
                            val notifyType = json.optInt("mark")
                            val customNotify =
                                MessageUtils.parseCustomNotify(message.text, notifyType)
                            if (customNotify != null) {
                                notifyCustomNotifyListener(customNotify)
                            }
                        }

                        "message" -> {
                            /**
                             * 解析收到的消息
                             */
                            val imMessage = MessageUtils.parseJsonToImMessage(message.text)
                            if (imMessage != null && imMessage.message!!.intoDb()) {
                                scope.launch {
                                    IMCore.getService(MessageService::class.java)
                                        .insertMessage(imMessage, false)
                                    val conversation =
                                        IMCore.getService(ConversationService::class.java)
                                            .getConversation(imMessage.channel)
                                    if (conversation != null) {
                                        notifyConversationListener(conversation)
                                    }
                                }
                            }

                            if (imMessage != null) {
                                notifyMsgListener(imMessage)
                            }
                        }
                    }

                } catch (ex: JSONException) {
                    ex.printStackTrace()
                }

            }
        }
    }

    fun init() {
        RtmManager.getInstance().addEventListener(imEventListener)
    }

    /**
     * 注册IM的状态监听
     * @param register true注册，false反注册
     * @param listener =IM状态的listener
     */
    fun registerStatusListener(register: Boolean, listener: IMStatusListener?) {
        if (listener == null) return
        synchronized(statusListeners) {
            val size = statusListeners.size
            var isFind: WeakReference<IMStatusListener>? = null
            for (i in size - 1 downTo 0) {
                val item = statusListeners[i]
                if (item.get() == listener) {
                    isFind = item
                }
            }
            if (register) {
                isFind?.let { statusListeners.remove(it) }
                statusListeners.add(WeakReference(listener))
            } else {
                isFind?.let { statusListeners.remove(it) }
            }
        }
    }

    /**
     * 注册IM的通知监听
     * @param register true注册，false反注册
     * @param listener =IM状态的listener
     */
    fun registerReceiveNotify(register: Boolean, listener: IMNotifyListener?) {
        if (listener == null) return
        synchronized(notifyListeners) {
            val size = notifyListeners.size
            var isFind: WeakReference<IMNotifyListener>? = null
            for (i in size - 1 downTo 0) {
                val item = notifyListeners[i]
                if (item.get() == listener) {
                    isFind = item
                }
            }
            if (register) {
                isFind?.let { notifyListeners.remove(it) }
                notifyListeners.add(WeakReference(listener))
            } else {
                isFind?.let { notifyListeners.remove(it) }
            }
        }
    }

    /**
     * 注册收到消息的监听
     */
    fun registerReceiveMessage(register: Boolean, listener: IMMessageListener?) {
        if (listener == null) return
        synchronized(messageListeners) {
            val size = messageListeners.size
            var isFind: WeakReference<IMMessageListener>? = null
            for (i in size - 1 downTo 0) {
                val item = messageListeners[i]
                if (item.get() == listener) {
                    isFind = item
                }
            }
            if (register) {
                isFind?.let { messageListeners.remove(it) }
                messageListeners.add(WeakReference(listener))
            } else {
                isFind?.let { messageListeners.remove(it) }
            }
        }
    }


    fun registerMsgSendStatusListener(register: Boolean, listener: MsgSendResultListener?) {
        if (listener == null) return
        synchronized(sendResultListener) {
            val size = sendResultListener.size
            var isFind: WeakReference<MsgSendResultListener>? = null
            for (i in size - 1 downTo 0) {
                val item = sendResultListener[i]
                if (item.get() == listener) {
                    isFind = item

                }
            }
            if (register) {
                isFind?.let { sendResultListener.remove(it) }
                sendResultListener.add(WeakReference(listener))
            } else {
                isFind?.let { sendResultListener.remove(it) }
            }
        }
    }

    fun registerConversationListener(register: Boolean, listener: ConversationListener?) {
        if (listener == null) return
        synchronized(conversationListeners) {
            val size = conversationListeners.size
            var isFind: WeakReference<ConversationListener>? = null
            for (i in size - 1 downTo 0) {
                val item = conversationListeners[i]
                if (item.get() == listener) {
                    isFind = item
                }
            }

            if (register) {
                isFind?.let { conversationListeners.remove(it) }
                conversationListeners.add(WeakReference(listener))
            } else {
                isFind?.let { conversationListeners.remove(it) }
            }
        }
    }

    /**
     * 通知被踢
     */
    fun notifyKickOutListener() {
        synchronized(statusListeners) {
            for (listener in statusListeners) {
                listener.get()?.kickOut()
            }
        }
    }

    fun notifyReLoginListener() {
        synchronized(statusListeners) {
            for (listener in statusListeners) {
                listener.get()?.reLogin()
            }
        }
    }

    fun notifyRenewTokenListener() {
        synchronized(statusListeners) {
            for (listener in statusListeners) {
                listener.get()?.renewToken()
            }
        }
    }

    fun notifyLoginSuccessListener() {
        synchronized(statusListeners) {
            for (listener in statusListeners) {
                listener.get()?.loginSuccess()
            }
        }
    }

    /**
     * 通知收到通知
     */
    fun notifyCustomNotifyListener(notify: CustomNotify) {
        synchronized(notifyListeners) {
            for (listener in notifyListeners) {
                listener.get()?.onReceiveNotify(notify)
            }
        }
    }


    /**
     * 通知收到消息
     */
    fun notifyMsgListener(message: Msg) {
        synchronized(messageListeners) {
            for (listener in messageListeners) {
                listener.get()?.onReceiveMsg(message)
            }
        }
    }

    /**
     * 通知聊天会话的更新
     */
    fun notifyConversationListener(conversation: Conversation) {
        synchronized(conversationListeners) {
            for (listener in conversationListeners) {
                listener.get()?.onConversationChange(conversation)
            }
        }
    }

    /**
     * 通知聊天会话的被删除
     */
    fun notifyDeletedConversationListener(list: MutableList<Conversation>) {
        synchronized(conversationListeners) {
            for (listener in conversationListeners) {
                listener.get()?.onConversationDelete(list)
            }
        }
    }


    /**
     * 通知消息的发送状态
     */
    fun notifySendResultListener(code: Int, error: String?, message: Msg) {
        synchronized(sendResultListener) {
            for (listener in sendResultListener) {
                when (message.status) {
                    MessageStatus.SENDING -> {
                        listener.get()?.onSending(message)
                    }

                    MessageStatus.SUCCESS -> {
                        listener.get()?.onSendSuccess(message)
                    }

                    MessageStatus.FAIL -> {
                        listener.get()?.onSendFailure(code, error, message)
                    }
                }
            }
        }

    }

}