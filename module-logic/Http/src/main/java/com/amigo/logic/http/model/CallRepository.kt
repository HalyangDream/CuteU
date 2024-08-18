package com.amigo.logic.http.model

import android.os.Bundle
import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.call.CallInfoResponse
import com.amigo.logic.http.response.call.HeartState
import com.amigo.logic.http.response.call.InvitedCall
import com.amigo.logic.http.response.call.DeviceFunctionInfoResponse
import com.amigo.logic.http.service.CallService

class CallRepository : ApiRepository() {


    private val service by lazy { ApiClient.getService(CallService::class.java) }


    suspend fun deviceFunctionUnlockInfo(): ApiResponse<DeviceFunctionInfoResponse> {
        val param = HttpCommonParam.getCommonParam()
        return launchRequest { service.deviceFunctionUnlockInfo(param.toRequestBody()) }
    }

    suspend fun unlockDeviceFunction(id: Long): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("id", id)
        }
        return launchRequest { service.unlockDeviceFunction(param.toRequestBody()) }
    }


    suspend fun invitedCall(
        remoteId: Long, source: String, bundle: Bundle?
    ): ApiResponse<InvitedCall> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("remote_id", remoteId)
            put("source", source)
        }
        return launchRequestTakeDialogBundle({ service.invitedCall(param.toRequestBody()) }, bundle)
    }

    suspend fun matchInvitedCall(
        matchId: Long, source: String, bundle: Bundle?
    ): ApiResponse<InvitedCall> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("match_id", matchId)
            put("source", source)
        }
        return launchRequestTakeDialogBundle(
            { service.matchInvitedCall(param.toRequestBody()) },
            bundle
        )
    }

    suspend fun cancelCall(
        callId: String
    ): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("call_id", callId)
        }
        return launchRequest { service.cancelCall(param.toRequestBody()) }
    }


    suspend fun finishCall(
        callId: String, reason: String
    ): ApiResponse<Unit> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("call_id", callId)
            put("reason", reason)
        }
        return launchRequest { service.hangUp(param.toRequestBody()) }
    }

    suspend fun heartCall(
        callId: String
    ): ApiResponse<HeartState> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("call_id", callId)
        }
        return launchRequest { service.heartCall(param.toRequestBody()) }
    }

    suspend fun getCallInfo(
        callId: String
    ): ApiResponse<CallInfoResponse> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("call_id", callId)
        }
        return launchRequest { service.getCallInfo(param.toRequestBody()) }
    }

}