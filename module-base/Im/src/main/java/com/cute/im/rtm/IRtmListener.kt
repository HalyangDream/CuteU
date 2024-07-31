package com.cute.im.rtm

import io.agora.rtm.RtmMessage

interface IRtmListener {


     fun onRtmMustLogin()
     fun onRtmKickOut()

     fun onRtmTokenExpired()

     fun onRtmServerBanned()

     fun onRtmReceiveMessage(message: RtmMessage, channel: String?)

}