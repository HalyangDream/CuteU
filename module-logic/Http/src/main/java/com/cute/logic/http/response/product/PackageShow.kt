package com.cute.logic.http.response.product

import com.google.gson.annotations.SerializedName

data class PackageShow(val cover:String, @SerializedName("pop_code")val popCode:String)
