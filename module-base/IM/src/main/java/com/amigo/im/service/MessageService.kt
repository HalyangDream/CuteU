package com.amigo.im.service

import com.amigo.im.annotation.IMService
import com.amigo.im.bean.Msg
import com.amigo.im.cutom.CustomMessage
import com.amigo.im.service.impl.MessageServiceImpl

/**
 * author : mac
 * date   : 2022/5/11
 *
 */
@IMService(MessageServiceImpl::class)
interface MessageService {

    fun generateIMMessage(senderId: String, peerId: String, message: CustomMessage): Msg

    suspend fun sendMessage(peerId: String, message: CustomMessage)

    suspend fun queryMessageList(count: Long, userId: String, peerId: String): List<Msg>?

    suspend fun queryMessageList(count: Long, channel: String): List<Msg>?

    suspend fun queryTextMessageList(count: Long, channel: String): List<Msg>?

    suspend fun queryMessageList(count: Long, message: Msg): List<Msg>?

    suspend fun queryMessageByMessageId(messageId: String): Msg?

    suspend fun queryRecentReceiveMessage(count: Long, userId: String): List<Msg>?

    suspend fun insertMessage(message: Msg, notify: Boolean = true)

    suspend fun insertLocalMessage(
        senderId: String,
        peerId: String,
        message: CustomMessage,
        notify: Boolean = true
    )

    suspend fun insertLocalMessage(
        message: Msg,
        notify: Boolean = true
    )


    suspend fun updateMessage(message: Msg, notify: Boolean = false)

    suspend fun deleteMessage(message: Msg)

    suspend fun deleteMessageByChannel(channel: String)
}