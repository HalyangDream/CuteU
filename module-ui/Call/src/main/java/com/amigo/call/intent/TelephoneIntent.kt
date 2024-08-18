package com.amigo.call.intent

import android.content.Context
import com.amigo.basic.UserIntent

sealed class TelephoneIntent : UserIntent {

    data class LaunchCall(
        val context: Context, val caller: Long, val callee: Long, val source: String
    ) : TelephoneIntent()


    data class LaunchStrategyCall(
        val caller: Long, val callee: Long, val isFreeCall: Boolean, val source: String
    ) : TelephoneIntent()

    data class LaunchMatchCall(val caller: Long, val matchId: Long, val source: String) :
        TelephoneIntent()


    object TryResumeCall : TelephoneIntent()

    data class MakeCall(
        val caller: Long, val callee: Long, val source: String, val isStrategyCall: Boolean
    ) : TelephoneIntent()

    data class MakeMatchCall(
        val caller: Long, val matchId: Long, val source: String
    ) : TelephoneIntent()

    data class CancelCall(val callId: String?) : TelephoneIntent()


    object ProcessCall : TelephoneIntent()


    object StopRing : TelephoneIntent()

    data class FinishCommunication(val reason: String) : TelephoneIntent()

    data class FinishCall(val callId: String, val reason: String) : TelephoneIntent()

}
