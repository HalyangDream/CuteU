package com.cute.chat.intent

import com.cute.basic.UserIntent
import com.cute.im.bean.Msg
import java.io.File

sealed class ChatIntent : UserIntent {

    data class MessageList(val uid: String, val peerId: String, val isFirstLoad: Boolean) :
        ChatIntent()

    data class MessageListForAnchor(val anchor: Msg) : ChatIntent()
    data class GetAnchorInfo(val peerId: Long, val isFirst: Boolean) : ChatIntent()

    data class SendTextMessage(val uid: Long, val peerId: Long, val message: String) :
        ChatIntent()

    data class SendImageMessage(val uid: Long, val peerId: Long, val file: File) : ChatIntent()

    data class SendVideoMessage(val uid: Long, val peerId: Long, val file: File) : ChatIntent()

    data class BlockUser(val peerId: Long) : ChatIntent()
    data class UnBlockUser(val peerId: Long) : ChatIntent()

    data class ReportUser(val peerId: Long, val reportType: String) : ChatIntent()

}