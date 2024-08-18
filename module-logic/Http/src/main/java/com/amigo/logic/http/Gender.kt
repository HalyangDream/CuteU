package com.amigo.logic.http

import android.os.Parcel
import android.os.Parcelable

enum class Gender(val value: Int) {

    FEMALE(2),
    MALE(1),
    UNKNOWN(3),
}

fun getGender(value: Int): Gender {
    return when (value) {
        2 -> Gender.FEMALE
        1 -> Gender.MALE
        else -> Gender.UNKNOWN
    }
}