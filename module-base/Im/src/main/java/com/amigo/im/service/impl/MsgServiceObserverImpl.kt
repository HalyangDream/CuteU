package com.amigo.im.service.impl

import com.amigo.im.MessageObserver
import com.amigo.im.listener.ConversationListener
import com.amigo.im.listener.IMMessageListener
import com.amigo.im.listener.IMNotifyListener
import com.amigo.im.listener.IMStatusListener
import com.amigo.im.listener.MsgSendResultListener
import com.amigo.im.service.MsgServiceObserver


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