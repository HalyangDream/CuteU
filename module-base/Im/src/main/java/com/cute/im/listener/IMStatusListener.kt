package com.cute.im.listener

/**
 * author : mac
 * date   : 2022/1/24
 *
 */
interface IMStatusListener {

    fun loginSuccess()

    /**
     * 需要更换新token
     */
    fun renewToken()

    fun reLogin()

    /**
     * 被踢下线
     */
    fun kickOut()

    /**
     * 被服务器禁止登录
     */
    fun onServerBanned()
}