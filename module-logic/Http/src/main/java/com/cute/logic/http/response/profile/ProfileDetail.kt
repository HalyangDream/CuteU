package com.cute.logic.http.response.profile

import com.google.gson.annotations.SerializedName

data class ProfileDetail(
    val id: Long,
    @SerializedName("nick_name")
    val name: String,
    val avatar: String,
    val gender: Int,
    val age: Int,
    val sign: String?,
    val height: String?,
    val album: MutableList<ProfileAlbum>?,
    val tag: MutableList<ProfileTag>?
)

data class ProfileAlbum(
    @SerializedName("resource_id") val resourceId: Int,
    @SerializedName("video_cover") val videoCover: String?,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("is_video") val isVideo: Boolean
)

data class ProfileTag(
    @SerializedName("tab_img") val tabImg: String,
    @SerializedName("tab_color") val tabColor: String,
    @SerializedName("tab_content") val tabContent: String
)
