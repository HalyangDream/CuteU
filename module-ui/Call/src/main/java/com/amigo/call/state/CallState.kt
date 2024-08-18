package com.amigo.call.state

import com.amigo.basic.UserState
import com.amigo.im.bean.Msg
import com.amigo.logic.http.response.call.CallInfoResponse
import com.amigo.logic.http.response.call.DeviceFunctionInfo
import com.amigo.logic.http.response.user.ChatUserInfo

sealed class CallState : UserState {

    data class UserInfoResult(val userDetail: ChatUserInfo) : CallState()

    data class DeviceFunctionInfoResult(
        val cameraClose: DeviceFunctionInfo?,
        val cameraSwitch: DeviceFunctionInfo?,
        val voiceMute: DeviceFunctionInfo?
    ) : CallState()

    data class UnlockDeviceFunctionResult(val result: Boolean, val info: DeviceFunctionInfo) :
        CallState()

    data class FollowState(val isFollowState: Boolean) : CallState()

    data class StartCall(val callId: String, val uid: Int) : CallState()

    data class UpdateCallDuration(val durationSecond: Int) : CallState()

    data class FinishCallSuccess(val callId: String, val reason: String) : CallState()

    data class FinishCallFailure(val callId: String, val error: String) : CallState()

    data class HeartFinish(val callId: String) : CallState()

    data class CallInfoResult(val callInfoResponse: CallInfoResponse):CallState()

}
