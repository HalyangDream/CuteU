package com.amigo.im.service

import com.amigo.im.annotation.IMService
import com.amigo.im.bean.Msg
import com.amigo.im.cutom.CustomMessage
import com.amigo.im.service.impl.ChatRoomServiceImpl
import io.agora.rtm.ResultCallback


/**
 * author : mac
 * date   : 2022/5/12
 *
 */
@IMService(ChatRoomServiceImpl::class)
interface ChatRoomService {

    fun joinChatRoom(channel: String, result: (Int) -> Unit)

    fun leaveChatRoom(channel: String, result:(Int) -> Unit)

    fun sendMessage(channel: String, customMessage: CustomMessage, result: (Int, Msg?) -> Unit)
}