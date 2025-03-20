package com.amigo.im.listener

import com.amigo.im.bean.Msg


/**
 * author : mac
 * date   : 2021/12/30
 *
 */
interface IMMessageListener {

    fun onReceiveMsg(message: Msg)

}