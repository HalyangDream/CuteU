package com.amigo.logic.http.response.product

import com.google.gson.annotations.SerializedName

data class VipPowerInfoData(
    val cover: String,
    @SerializedName("small_icon") val smallCover: String,
    val title: String, val content: String
)

data class VipPowerInfoDataResponse(val list: MutableList<VipPowerInfoData>?)
