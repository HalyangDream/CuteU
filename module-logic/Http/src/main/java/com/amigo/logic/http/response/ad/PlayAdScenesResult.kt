package com.amigo.logic.http.response.ad

import com.google.gson.annotations.SerializedName

data class PlayAdScenesResult(@SerializedName("display_ad") val isPlayAd: Boolean)
