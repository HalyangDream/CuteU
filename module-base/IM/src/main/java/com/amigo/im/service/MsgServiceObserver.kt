package com.amigo.im.service

import com.amigo.im.annotation.IMService
import com.amigo.im.listener.ConversationListener
import com.amigo.im.listener.IMMessageListener
import com.amigo.im.listener.IMNotifyListener
import com.amigo.im.listener.IMStatusListener
import com.amigo.im.listener.MsgSendResultListener
import com.amigo.im.service.impl.MsgServiceObserverImpl

/**
 * author : mac
 * date   : 2022/5/12
 *
 */
@IMService(MsgServiceObserverImpl::class)
interface MsgServiceObserver {

    fun observerReceiveNotify(listener: IMNotifyListener?, register:Boolean)

    fun observerReceiveMessage(listener: IMMessageListener?, register:Boolean)

    fun observerConversationChange(listener: ConversationListener?, register:Boolean)

    fun observerMsgSendStatus(listener: MsgSendResultListener?, register:Boolean)

    fun observerIMStatus(listener: IMStatusListener?, register:Boolean)
}