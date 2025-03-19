package com.amigo.logic.http.response.product

import com.google.gson.annotations.SerializedName

data class PopShow(
    @SerializedName("is_show")
    val isShow: Boolean,
    @SerializedName("pop_code")
    val popCode: String
)

