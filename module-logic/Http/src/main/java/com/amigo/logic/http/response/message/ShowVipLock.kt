package com.amigo.logic.http.response.message

import com.google.gson.annotations.SerializedName

data class ShowVipLock(@SerializedName("is_send")val hasUnLock:Boolean)