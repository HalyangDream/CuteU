package com.cute.mine.bean

import android.os.Parcel
import android.os.Parcelable

data class Language(
    val code: String,
    val name: String,
    val country: String,
    val icon: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
        parcel.writeString(country)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Language> {
        override fun createFromParcel(parcel: Parcel): Language {
            return Language(parcel)
        }

        override fun newArray(size: Int): Array<Language?> {
            return arrayOfNulls(size)
        }

        fun createLanguageList(): MutableList<Language> {
            val list = mutableListOf<Language>().apply {
                add(Language("en", "English", "US", ""))
                add(Language("pt", "Portugues do Brasil", "BR", ""))
                add(Language("hi", "हिंदी", "IN", ""))
                add(Language("in", "Bahasa Indonesia", "ID", ""))
                add(Language("es", "Español", "AR", ""))
            }
            return list
        }
    }

}

