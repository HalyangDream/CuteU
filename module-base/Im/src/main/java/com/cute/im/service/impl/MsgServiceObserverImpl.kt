package com.cute.im.service.impl

import com.cute.im.MessageObserver
import com.cute.im.listener.ConversationListener
import com.cute.im.listener.IMMessageListener
import com.cute.im.listener.IMNotifyListener
import com.cute.im.listener.IMStatusListener
import com.cute.im.listener.MsgSendResultListener
import com.cute.im.service.MsgServiceObserver


/**
 * author : mac
 * date   : 2022/5/12
 *
 */
class MsgServiceObserverImpl : MsgServiceObserver {

    override fun observerReceiveNotify(listener: IMNotifyListener?, register: Boolean) {
        MessageObserver.registerReceiveNotify(register, listener)
    }

    override fun observerReceiveMessage(listener: IMMessageListener?, register: Boolean) {
        MessageObserver.registerReceiveMessage(register, listener)
    }

    override fun observerConversationChange(listener: ConversationListener?, register: Boolean) {
        MessageObserver.registerConversationListener(register, listener)
    }

    override fun observerMsgSendStatus(listener: MsgSendResultListener?, register: Boolean) {
        MessageObserver.registerMsgSendStatusListener(register, listener)
    }

    override fun observerIMStatus(listener: IMStatusListener?, register: Boolean) {
        MessageObserver.registerStatusListener(register, listener)
    }
}