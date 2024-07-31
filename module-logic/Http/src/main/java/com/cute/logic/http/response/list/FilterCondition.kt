package com.cute.logic.http.response.list

import com.google.gson.annotations.SerializedName


data class FilterCondition(
    @SerializedName("feeling") val feeling: MutableList<Filter>?,
    @SerializedName("language") val language: MutableList<Filter>?,
    @SerializedName("region") val region: MutableList<Filter>?,
    @SerializedName("country") val country: MutableList<Filter>?,
)

data class Filter(
    val id: String, val content: String
)
