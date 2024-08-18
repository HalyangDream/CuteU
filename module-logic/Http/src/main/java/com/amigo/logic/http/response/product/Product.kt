package com.amigo.logic.http.response.product

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    @SerializedName("sub_name")
    val subName: String?,
    val cover: String?,
    @SerializedName("goo_price")
    val googlePrice: String,
    @SerializedName("display_price")
    val displayPrice: String,
    @SerializedName("display_unit")
    val unit: String,
    val google: String,
    val discount: String?,
    @SerializedName("bonus_describe")
    val bonusDescribe: String?,
    @SerializedName("describe")
    val describe: String?,
    @SerializedName("is_subscribe")
    val isSubscribe: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte().toInt() != 0

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(subName)
        parcel.writeString(cover)
        parcel.writeString(googlePrice)
        parcel.writeString(displayPrice)
        parcel.writeString(unit)
        parcel.writeString(google)
        parcel.writeString(discount)
        parcel.writeString(bonusDescribe)
        parcel.writeString(describe)
        parcel.writeByte((if (isSubscribe) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}


data class ProductResponse(
    @SerializedName("extra_product") val extraProduct: MutableList<Product>?,
    val list: MutableList<Product>?
)


data class DisposableProductResponse(
    @SerializedName("old_product") val oldProduct: Product?,
    @SerializedName("new_product") val newProduct: Product?,
)

