package com.amigo.logic.http.response.user

import com.amigo.logic.http.response.User
import com.google.gson.annotations.SerializedName

data class ChatUserInfo(
    @SerializedName("is_block")
    val isBlock: Boolean,
    @SerializedName("is_follow")
    val isFollow: Boolean,
    @SerializedName("call_price")
    val callPrice: String?,
    val city: String = "",
    val country: String = "",
    @SerializedName("country_img")
    val countryImg: String? = "",
    val album: MutableList<ChatUserAlbum>?
) : User()

data class ChatUserAlbum(
    @SerializedName("res_id")
    val resId: String,
    @SerializedName("video_cover")
    val videoCover: String?,
    @SerializedName("res_url")
    val resUrl: String,
    @SerializedName("is_lock") val isLock: Boolean,
    @SerializedName("is_video") val isVideo: Boolean
)
