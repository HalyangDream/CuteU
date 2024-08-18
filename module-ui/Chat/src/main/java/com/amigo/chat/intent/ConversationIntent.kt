package com.amigo.chat.intent

import com.amigo.basic.ListIntent
import com.amigo.basic.UserIntent
import com.amigo.im.bean.Conversation

sealed class ConversationIntent : UserIntent {


    data class GetOfficialAccount(val userId: String) : ConversationIntent()
    data class LoadData(val userId: String) : ConversationIntent()

    data class LoadMoreData(val anchor: Conversation) : ConversationIntent()

}
