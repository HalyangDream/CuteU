package com.amigo.logic.http.response

import com.google.gson.annotations.SerializedName

open class User {
    @SerializedName("user_id")
    var id: Long=0
    @SerializedName("nick_name")
    var name: String = ""
    var avatar: String = ""
    var age: Int = 18
    var online: Int = 1
    var gender: Int = 1
}
