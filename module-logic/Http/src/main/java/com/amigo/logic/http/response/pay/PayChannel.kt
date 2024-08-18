package com.amigo.logic.http.response.pay

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Payment(
    val id: Int,
    val name: String,
    val img: String,
    val type: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(img)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }
}

data class PaymentResponse(@SerializedName("list")val payment: ArrayList<Payment>?)
