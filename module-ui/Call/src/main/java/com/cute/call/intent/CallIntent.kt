package com.cute.call.intent

import com.cute.basic.UserIntent
import com.cute.logic.http.response.call.DeviceFunctionInfo

sealed class CallIntent : UserIntent {

    data class GetAnchorInfo(val anchorId: Long) : CallIntent()
    object RequestDeviceFunction : CallIntent()
    data class UnlockDeviceFunction(val info: DeviceFunctionInfo) : CallIntent()
    data class Like(val anchorId: Long) : CallIntent()
    data class UnLike(val anchorId: Long) : CallIntent()
    data class StartCall(val callId: String, val anchorId: Long) : CallIntent()
    data class CallInfo(val callId: String) : CallIntent()
    data class FinishCall(val callId: String, val reason: String) : CallIntent()
}
