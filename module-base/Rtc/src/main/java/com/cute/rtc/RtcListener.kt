package com.cute.rtc

import android.util.Log
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler

internal class RtcListener : IRtcEngineEventHandler() {


    private val iListeners by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { mutableListOf<IRtcListener>() }

    var currentChannel: String? = ""


    fun addRtcListener(listener: IRtcListener) {
        synchronized(iListeners) {
            iListeners.add(listener)
        }
    }

    fun removeRtcListener(listener: IRtcListener) {
        synchronized(iListeners) {
            iListeners.remove(listener)
        }
    }

    private fun notifyRtcListeners(block: (IRtcListener) -> Unit) {
        synchronized(iListeners) {
            iListeners.forEach {
                block(it)
            }
        }
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onJoinChannelSuccess(channel, uid, elapsed)
        currentChannel = channel
        notifyRtcListeners {
            it.onJoinChannelSuccess(channel, uid, elapsed)
        }
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        super.onUserJoined(uid, elapsed)
        notifyRtcListeners {
            it.onUserJoined(currentChannel, uid, elapsed)
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        super.onUserOffline(uid, reason)
        Log.i("RtcListener","onUserOffline reason:$reason")
        Log.i("RtcListener","onUserOffline uid:$uid")
        if (reason == Constants.USER_OFFLINE_QUIT || reason == Constants.USER_OFFLINE_DROPPED) {
            notifyRtcListeners { it.onUserOffline(currentChannel, uid, reason) }
        }
    }

    override fun onUserMuteVideo(uid: Int, muted: Boolean) {
        super.onUserMuteVideo(uid, muted)
        notifyRtcListeners { it.onUserMuteVideo(currentChannel, uid, muted) }
    }

    override fun onUserMuteAudio(uid: Int, muted: Boolean) {
        super.onUserMuteAudio(uid, muted)
        notifyRtcListeners { it.onUserMuteAudio(currentChannel, uid, muted) }
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        super.onNetworkQuality(uid, txQuality, rxQuality)
        notifyRtcListeners { it.onNetworkQuality(currentChannel, uid, txQuality, rxQuality) }
    }

    override fun onLeaveChannel(stats: RtcStats?) {
        super.onLeaveChannel(stats)
        currentChannel = ""
    }

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        super.onConnectionStateChanged(state, reason)
        Constants.CONNECTION_STATE_CONNECTED
        if (reason == Constants.CONNECTION_CHANGED_BANNED_BY_SERVER) {
            notifyRtcListeners { it.onServerBanned(currentChannel) }
        }

        if (reason == Constants.CONNECTION_CHANGED_JOIN_FAILED) {
            notifyRtcListeners { it.onJoinChannelFailure(currentChannel) }
        }

        if (reason == Constants.CONNECTION_CHANGED_INVALID_APP_ID) {
        }

        if (reason == Constants.CONNECTION_CHANGED_INVALID_CHANNEL_NAME) {
        }

        if (reason == Constants.CONNECTION_CHANGED_INVALID_TOKEN) {
        }

    }
}