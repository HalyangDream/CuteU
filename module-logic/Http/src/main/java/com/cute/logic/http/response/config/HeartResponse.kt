package com.cute.logic.http.response.config

import com.google.gson.annotations.SerializedName

data class HeartResponse(@SerializedName("follow_notify") val followNotify: FollowNotify?)


data class FollowNotify(
    @SerializedName("unread_num") val unreadNum: Int?
)