package com.cute.im.service.impl

import android.util.Log
import com.cute.im.IMCore
import com.cute.im.MessageObserver
import com.cute.im.MessageUtils
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomMessage
import com.cute.im.rtm.RtmManager
import com.cute.im.service.ChatRoomService
import com.cute.im.service.MessageService
import com.cute.im.service.UserService
import io.agora.rtm.*

/**
 * author : mac
 * date   : 2022/5/12
 *
 */
class ChatRoomServiceImpl : ChatRoomService, RtmChannelListener {

    private val rtmChannelMap by lazy { mutableMapOf<String, RtmChannel>() }


    override fun joinChatRoom(channel: String, result: (Int) -> Unit) {
        val rtmChannel = RtmManager.getInstance().getChannel(channel, this)
        if (rtmChannel == null) {
            result.invoke(-1)
            return
        }
        rtmChannel.join(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                rtmChannelMap[channel] = rtmChannel
                result.invoke(0)
            }

            override fun onFailure(p0: ErrorInfo?) {
                result.invoke(p0!!.errorCode)
            }
        })
    }


    override fun leaveChatRoom(channel: String, result: (Int) -> Unit) {
        synchronized(rtmChannelMap) {
            val rtmChannel = rtmChannelMap[channel]
            if (rtmChannel == null) {
                result.invoke(-1)
                return
            }
            rtmChannel.leave(object : io.agora.rtm.ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    result.invoke(0)
                }

                override fun onFailure(p0: ErrorInfo?) {
                    result.invoke(p0!!.errorCode)
                }
            })
        }
    }


    override fun sendMessage(
        channel: String,
        customMessage: CustomMessage,
        result: (Int, Msg?) -> Unit
    ) {
        synchronized(rtmChannelMap) {
            val rtmChannel = rtmChannelMap[channel]
            if (rtmChannel == null) {
                result.invoke(-1, null)
                return
            }

            val userId = IMCore.getService(UserService::class.java).getLoginUserId()
            if (userId.isNullOrEmpty()) {
                result.invoke(-1, null)
                return
            }
            val rtmMsg = RtmManager.getInstance().createRtmMessage()
            val imMessage = IMCore.getService(MessageService::class.java)
                .generateIMMessage(userId, channel, customMessage)
            imMessage.status = MessageStatus.SENDING
            rtmMsg?.text = imMessage.toJson()
            val options = SendMessageOptions()
            rtmChannel.sendMessage(rtmMsg, options, object : io.agora.rtm.ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    imMessage.status = MessageStatus.SUCCESS
                    result.invoke(0, imMessage)
                }

                override fun onFailure(p0: ErrorInfo?) {
                    imMessage.status = MessageStatus.FAIL
                    result.invoke(p0!!.errorCode, imMessage)
                }
            })
        }
    }


    //====================RtmChannel的listener
    override fun onMemberCountUpdated(p0: Int) {

    }

    override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {

    }

    override fun onMessageReceived(rtmMessage: RtmMessage?, p1: RtmChannelMember?) {
        if (rtmMessage == null) return
        val imMessage = MessageUtils.parseJsonToImMessage(rtmMessage.text)
        if (imMessage == null) return
        imMessage.channel = p1!!.channelId
        MessageObserver.notifyMsgListener(imMessage)
    }


    override fun onMemberJoined(p0: RtmChannelMember?) {

    }

    override fun onMemberLeft(p0: RtmChannelMember?) {

    }
}