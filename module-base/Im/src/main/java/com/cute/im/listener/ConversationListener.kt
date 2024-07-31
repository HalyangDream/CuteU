package com.cute.im.listener

import com.cute.im.bean.Conversation


/**
 * author : mac
 * date   : 2021/12/30
 *
 */
interface ConversationListener {

    fun onConversationChange(conversation: Conversation)

    fun onConversationDelete(list: MutableList<Conversation>)

}