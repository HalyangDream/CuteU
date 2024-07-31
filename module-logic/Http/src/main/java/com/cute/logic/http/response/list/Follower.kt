package com.cute.logic.http.response.list

import com.google.gson.annotations.SerializedName
import com.cute.logic.http.response.User

data class MyLike(
    val country: String,
    val city: String,
    @SerializedName("is_blur") val isBlur: Boolean
) : User()

data class MyLikeResponse(val list: MutableList<MyLike>?)
