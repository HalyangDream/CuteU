package com.cute.chat.intent

import com.cute.basic.ListIntent
import com.cute.basic.UserIntent
import com.cute.im.bean.Conversation

sealed class ConversationIntent : UserIntent {


    data class GetOfficialAccount(val userId: String) : ConversationIntent()
    data class LoadData(val userId: String) : ConversationIntent()

    data class LoadMoreData(val anchor: Conversation) : ConversationIntent()

}
