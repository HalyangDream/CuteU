package com.amigo.logic.http.response.pay

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("order_no") val orderNo: String,
    @SerializedName("pay_url") val payUrl: String?
)
