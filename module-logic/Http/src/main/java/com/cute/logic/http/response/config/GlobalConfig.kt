package com.cute.logic.http.response.config

import com.google.gson.annotations.SerializedName

data class GlobalConfig(
    @SerializedName("display_google_login")
    val displayGoogleLogin: Boolean,
    @SerializedName("tab_index")
    val mainTabIndex: Int = 0,
    @SerializedName("show_match")
    val displayMatchPage: Boolean
)
