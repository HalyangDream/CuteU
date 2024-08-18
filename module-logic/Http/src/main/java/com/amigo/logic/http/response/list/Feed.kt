package com.amigo.logic.http.response.list

import com.amigo.logic.http.response.User
import com.google.gson.annotations.SerializedName

data class Feed(
    @SerializedName("call_price") val callPrice: String,
    @SerializedName("is_follow") var isFollow: Boolean,
    val city: String?,
    val country: String?
) : User()

data class FeedResponse(val list: MutableList<Feed>)
