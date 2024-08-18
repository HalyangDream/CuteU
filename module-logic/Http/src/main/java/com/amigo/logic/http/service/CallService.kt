package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import com.amigo.logic.http.response.call.CallInfoResponse
import com.amigo.logic.http.response.call.HeartState
import com.amigo.logic.http.response.call.InvitedCall
import com.amigo.logic.http.response.call.DeviceFunctionInfoResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface CallService {

    @POST("/v1/call/device_function")
    suspend fun deviceFunctionUnlockInfo(@Body body: RequestBody): ApiResponse<DeviceFunctionInfoResponse>

    @POST("/v1/call/unlock_device_function")
    suspend fun unlockDeviceFunction(@Body body: RequestBody): ApiResponse<Unit>

    /**
     * 发起通话
     */
    @POST("/v1/call/invited")
    suspend fun invitedCall(@Body body: RequestBody): ApiResponse<InvitedCall>

    /**
     * 匹配通话
     */
    @POST("/v1/call/invited_match")
    suspend fun matchInvitedCall(@Body body: RequestBody): ApiResponse<InvitedCall>

    /**
     * 取消通话
     */
    @POST("/v1/call/cancel")
    suspend fun cancelCall(@Body body: RequestBody): ApiResponse<Unit>


    /**
     * 关闭通话
     */
    @POST("/v1/call/hangup")
    suspend fun hangUp(@Body body: RequestBody): ApiResponse<Unit>

    /**
     * 通话心跳
     */
    @POST("/v1/call/heart")
    suspend fun heartCall(@Body body: RequestBody): ApiResponse<HeartState>

    @POST("/v1/call/info")
    suspend fun getCallInfo(@Body body: RequestBody): ApiResponse<CallInfoResponse>
}