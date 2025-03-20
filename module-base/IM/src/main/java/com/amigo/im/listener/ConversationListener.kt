package com.amigo.im.listener

import com.amigo.im.bean.Conversation


/**
 * author : mac
 * date   : 2021/12/30
 *
 */
interface ConversationListener {

    fun onConversationChange(conversation: Conversation)

    fun onConversationDelete(list: MutableList<Conversation>)

}