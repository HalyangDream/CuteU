package com.amigo.logic.http.response.call

import com.google.gson.annotations.SerializedName

data class InvitedCall(
    @SerializedName("remote_id")
    val remoteId: Long,
    @SerializedName("call_id")
    val callId: String,
    @SerializedName("video_url")
    val videoUrl: String,
    @SerializedName("play_end_time")
    val playEndTime: Int?,
    @SerializedName("into_connect_state_duration")
    val intoConnectStateDuration: Int,
    @SerializedName("load_video_wait_duration")
    val loadVideoWaitDuration: Int,
)

