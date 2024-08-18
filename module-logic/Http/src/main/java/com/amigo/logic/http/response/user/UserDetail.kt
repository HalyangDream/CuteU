package com.amigo.logic.http.response.user

import com.google.gson.annotations.SerializedName
import com.amigo.logic.http.response.User

data class UserDetail(
    @SerializedName("is_follow")
    val isFollow: Boolean,
    @SerializedName("is_block")
    val isBlock: Boolean,
    val sign: String,
    val city: String = "",
    val country: String = "",
    @SerializedName("call_price")
    val callPrice: String?,
    @SerializedName("follower_num")
    val followerNum: String = "",
    @SerializedName("followed_num")
    val followedNum: String = "",
    val info: MutableList<UserBaseInfo>?,
    val tag: MutableList<UserTag>?,
    val album: MutableList<UserAlbum>?
) : User()


data class UserBaseInfo(val title: String, val content: String)

data class UserTag(
    @SerializedName("tab_img") val tabImg: String,
    @SerializedName("tab_color") val tabColor: String,
    @SerializedName("tab_content") val tabContent: String
)

data class UserAlbum(
    @SerializedName("res_id")
    val resId: String,
    @SerializedName("video_cover")
    val videoCover: String?,
    @SerializedName("res_url")
    val resUrl: String,
    @SerializedName("is_lock") val isLock: Boolean,
    @SerializedName("is_video") val isVideo: Boolean
)