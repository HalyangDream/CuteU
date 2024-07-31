package com.cute.im.listener

import com.cute.im.bean.Msg


/**
 * author : mac
 * date   : 2021/12/30
 *
 */
interface IMMessageListener {

    fun onReceiveMsg(message: Msg)

}