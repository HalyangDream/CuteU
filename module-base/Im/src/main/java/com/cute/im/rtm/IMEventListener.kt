package com.cute.im.rtm

import io.agora.rtm.RtmMessage

/**
 * author : mac
 * date   : 2022/4/13
 *
 */
open class IMEventListener : IRtmListener {


    override fun onRtmMustLogin() {
        onMustLogin()
    }

    override fun onRtmKickOut() {
        onKickOut()
    }

    override fun onRtmTokenExpired() {
        onTokenExpired()
    }

    override fun onRtmServerBanned() {
        onKickOut()
    }

    override fun onRtmReceiveMessage(message: RtmMessage, channel: String?) {
        onReceiveMessage(message, channel)
    }

    open fun onMustLogin() {}
    open fun onKickOut() {}

    open fun onTokenExpired() {}

    open fun onReceiveMessage(message: RtmMessage, channel: String?) {}

}