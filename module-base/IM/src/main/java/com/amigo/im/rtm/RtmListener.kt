package com.amigo.im.rtm

import android.util.Log
import com.amigo.im.IMCore
import com.amigo.im.service.UserService
import io.agora.rtm.*

internal class RtmListener : RtmClientListener {

    private val iRtmListeners by lazy {
        mutableListOf<IRtmListener?>()
    }

    fun addRtmListener(iRtmListener: IRtmListener?) {
        if (iRtmListener == null) {
            return
        }
        synchronized(iRtmListeners) {
            iRtmListeners.remove(iRtmListener)
            iRtmListeners.add(iRtmListener)
        }
    }

    fun removeRtmListener(iRtmListener: IRtmListener?) {
        if (iRtmListener == null) {
            return
        }
        synchronized(iRtmListeners) {
            iRtmListeners.remove(iRtmListener)
        }
    }

    private fun notifyRtmListeners(block: (IRtmListener?) -> Unit) {
        synchronized(iRtmListeners) {
            iRtmListeners.forEach {
                block(it)
            }
        }
    }

    override fun onConnectionStateChanged(status: Int, reason: Int) {
        if (status == RtmStatusCode.ConnectionState.CONNECTION_STATE_DISCONNECTED) {
            notifyRtmListeners {
                it?.onRtmMustLogin()
            }
        }

        if (status == RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED) {
            notifyRtmListeners {
                it?.onRtmKickOut()
            }
        }

        if (reason == RtmStatusCode.ConnectionChangeReason.CONNECTION_CHANGE_REASON_BANNED_BY_SERVER) {
            notifyRtmListeners {
                it?.onRtmServerBanned()
            }
        }
    }

    override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
        if (p0 == null) return
        notifyRtmListeners {
            it?.onRtmReceiveMessage(p0, p1)
        }
    }


    override fun onTokenExpired() {
        notifyRtmListeners {
            it?.onRtmTokenExpired()
        }
    }

    override fun onTokenPrivilegeWillExpire() {

    }

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {

    }
}