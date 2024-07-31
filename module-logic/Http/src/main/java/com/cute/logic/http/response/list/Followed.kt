package com.cute.logic.http.response.list

import com.cute.logic.http.response.User
import com.google.gson.annotations.SerializedName

data class LikeMe(
    val country: String,
    val city: String,
    @SerializedName("is_blur") val isBlur: Boolean
) : User()

data class LikeMeResponse(val list: MutableList<LikeMe>?)
