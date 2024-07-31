package com.cute.im.service

import com.cute.im.annotation.IMService
import com.cute.im.listener.ConversationListener
import com.cute.im.listener.IMMessageListener
import com.cute.im.listener.IMNotifyListener
import com.cute.im.listener.IMStatusListener
import com.cute.im.listener.MsgSendResultListener
import com.cute.im.service.impl.MsgServiceObserverImpl

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