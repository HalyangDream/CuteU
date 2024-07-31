package com.cute.logic.http.response.list

import com.google.gson.annotations.SerializedName
import com.cute.logic.http.response.User

data class BlackUser(val country: String, @SerializedName("country_img") val countryImg: String) :
    User()

data class BlackListResponse(val list: MutableList<BlackUser>?)
