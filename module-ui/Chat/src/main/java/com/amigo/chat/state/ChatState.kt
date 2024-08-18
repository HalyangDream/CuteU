package com.amigo.chat.state

import com.amigo.basic.UserState
import com.amigo.im.bean.Msg
import com.amigo.logic.http.response.user.ChatUserInfo
import com.amigo.logic.http.response.user.UserDetail

sealed class ChatState : UserState {

    data class MessageListResult(
        val data: List<Msg>?,
        val isFirstLoad: Boolean = true
    ) : ChatState()

    data class AnchorInfo(val isFirst: Boolean, val anchorInfo: ChatUserInfo?) : ChatState()

    data class BlockUserResult(val result: Boolean) : ChatState()

    data class UnBlockUserResult(val result: Boolean) : ChatState()

    data class ReportUserResult(val result: Boolean) : ChatState()
}
