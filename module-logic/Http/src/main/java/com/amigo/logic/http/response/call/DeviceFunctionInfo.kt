package com.amigo.logic.http.response.call

import com.google.gson.annotations.SerializedName

data class DeviceFunctionInfo(
    val id: Long,
    val enable: Boolean,
    @SerializedName("unlock_price") val unlockPrice: String,
    val type: String
)

data class DeviceFunctionInfoResponse(val list: MutableList<DeviceFunctionInfo>?)

enum class DeviceFunctionEnum {
    CAMERA_CLOSE, CAMERA_SWITCH, VOICE_MUTE
}
