package com.amigo.im.listener

import com.amigo.im.bean.Msg


/**
 * author : mac
 * date   : 2022/1/19
 *
 */
interface MsgSendResultListener {

    fun onSending(message: Msg)

    fun onSendSuccess(message: Msg)

    fun onSendFailure(code: Int, error: String?, message: Msg)
}