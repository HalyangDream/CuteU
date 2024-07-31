package com.cute.im.service

import com.cute.im.annotation.IMService
import com.cute.im.bean.Msg
import com.cute.im.cutom.CustomMessage
import com.cute.im.service.impl.ChatRoomServiceImpl
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