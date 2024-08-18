package com.amigo.rtc

interface IRtcListener {

    /**
     * 加入频道成功
     */
    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)

    /**
     * 加入频道失败
     */
    fun onJoinChannelFailure(channel: String?)

    /**
     * 远端用户加入
     */
    fun onUserJoined(channel: String?, uid: Int, elapsed: Int)

    /**
     * 远端用户离开
     */
    fun onUserOffline(channel: String?, uid: Int, reason: Int)

    /**
     * 远端用户是否静音
     */
    fun onUserMuteAudio(channel: String?, uid: Int, muted: Boolean)

    /**
     * 远端用户是否发送视频
     */
    fun onUserMuteVideo(channel: String?, uid: Int, muted: Boolean)

    /**
     * 远端用户的网络质量
     */
    fun onNetworkQuality(channel: String?, uid: Int, txQuality: Int, rxQuality: Int)

    /**
     * 被服务器禁止
     */
    fun onServerBanned(channel: String?)


}