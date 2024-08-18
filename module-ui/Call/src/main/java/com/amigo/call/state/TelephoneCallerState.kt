package com.amigo.call.state

import com.amigo.basic.UserState

sealed class TelephoneCallerState : UserState {

    //接口状态
    data class OperateInvitedSuccess(val caller: Long, val callee: Long, val callId: String) :
        TelephoneCallerState()

    data class OperateInvitedFailure(val code: String, val error: String) : TelephoneCallerState()

    object OperateCancelInvitedSuccess : TelephoneCallerState()

    data class OperateCancelInvitedFailure(val code: String, val error: String) :
        TelephoneCallerState()

    object OperateCalleeBusy : TelephoneCallerState()

    object OperateCalleeOffline : TelephoneCallerState()

    object Connecting : TelephoneCallerState()

    data class CanPlayVideo(val canPlay: Boolean) : TelephoneCallerState()

    data class StartLoadVideo(val videoUrl: String, val videoEndTime: Int?) : TelephoneCallerState()


    data class FinishCommunication(val reason: String) : TelephoneCallerState()

    data class FinishCallException(val reason: String) : TelephoneCallerState()

    data class FinishCall(val channel: String, val reason: String) : TelephoneCallerState()

}
