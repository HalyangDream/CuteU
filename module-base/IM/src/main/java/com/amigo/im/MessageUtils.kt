package com.amigo.im

import com.amigo.im.bean.MessageStatus
import com.amigo.im.bean.Msg
import com.amigo.im.cutom.CustomMessage
import com.amigo.im.cutom.CustomNotify
import io.agora.rtm.RtmMessage
import org.json.JSONException
import org.json.JSONObject

/**
 * author : mac
 * date   : 2022/5/11
 *
 */
internal object MessageUtils {

    val registerMessages by lazy { mutableListOf<CustomMessage>() }
    val registerNotify by lazy { mutableListOf<CustomNotify>() }

    /**
     * 解析RtmMessage到IMMessage
     * @param message RtmMessage
     * @return 内部定义的IM的消息
     */
    fun parseJsonToImMessage(json: String): Msg? {
        try {
            val jsonObject = JSONObject(json)
            val messageType = jsonObject.optInt("mark", 1)
            return Msg(
                type = "message",
                channel = jsonObject.optString("channel_id"),
                messageId = jsonObject.optString("message_id", ""),
                timeStamp = System.currentTimeMillis(),
                mark = messageType,
                sendId = jsonObject.optString("sender_id", ""),
                receiveId = jsonObject.optString("receiver_id", ""),
                originJson = json,
                status = MessageStatus.SUCCESS,
            ).apply {
                message = parseCustomMsgToIMMessage(json, messageType)
            }


        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
        return null
    }

    /**
     * 解析自定义消息到具体实现
     */
    fun parseCustomMsgToIMMessage(json: String, messageType: Int): CustomMessage? {
        val customMessage = getCustomMessageByType(messageType)
        customMessage?.parseJson(json)
        return customMessage
    }

    /**
     * 解析自定义通知到具体实现
     */
    fun parseCustomNotify(json: String, notifyType: Int): CustomNotify? {
        val customNotify = getCustomNotifyByType(notifyType)
        customNotify?.parseJson(json)
        return customNotify
    }


    /**
     * 获取CustomMessage的类型
     */
    private fun getCustomMessageByType(messageType: Int): CustomMessage? {
        for (registerMessage in registerMessages) {
            if (registerMessage.identity() == messageType) {
                val rClass = registerMessage::class.java
                return rClass.newInstance()
            }
        }
        return null
    }

    /**
     * 获取CustomNotify的类型
     */
    private fun getCustomNotifyByType(notifyType: Int): CustomNotify? {
        for (notify in registerNotify) {
            if (notify.notifyType() == notifyType) {
                val rClass = notify::class.java
                return rClass.newInstance()
            }
        }
        return null
    }
}