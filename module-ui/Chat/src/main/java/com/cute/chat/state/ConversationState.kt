package com.cute.chat.state

import com.cute.basic.UserState
import com.cute.im.bean.Conversation

sealed class ConversationState : UserState {

    data class HeaderData(val data: MutableList<Conversation>?, val officialAccount: String) :
        ConversationState()

    data class ConversationData(val isBottom: Boolean, val data: List<Conversation>?) :
        ConversationState()


    data class LoadMoreConversationData(val isBottom: Boolean, val data: List<Conversation>?) :
        ConversationState()

}
