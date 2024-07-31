package com.cute.logic.http.response.list

import com.google.gson.annotations.SerializedName
import com.cute.logic.http.response.User

data class VideoList(
    val location: String,
    @SerializedName("video_cover") val videoCover: String,
    @SerializedName("video_url") val videoUrl: String,
    @SerializedName("call_price") val callPrice: String,
    @SerializedName("is_follow") var isFollow: Boolean,
    @SerializedName("follow_num") var followNum: Int,
    @SerializedName("user_tag") val userTag: MutableList<VideoListTag>?
) : User()


data class VideoListTag(
    @SerializedName("tag_img") val tagImg: String,
    @SerializedName("tag_content") val tagContent: String
)

data class VideoListResponse(
    val list: MutableList<VideoList>?
)
