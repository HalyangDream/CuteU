package com.cute.im.bean

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

@Entity(indices = [Index(value = ["uid"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var uid: String,
    var name: String,
    var avatar: String? = null,
    var extra: String? = null,
    var time: Long? = 0
) {
    @Ignore
    var extraMap: MutableMap<String, Any>? = null
    fun toMap() {
        this.extraMap = mutableMapOf<String, Any>()
        if (TextUtils.isEmpty(extra)) return
        try {
            val jsonObject: JSONObject = JSONObject(extra)
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                extraMap!![key] = jsonObject.get(key)
            }
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }

    fun toExtra() {
        if (extraMap == null) {
            extra = ""
            return
        }
        try {
            val jsonObject = JSONObject()
            for (entry in extraMap!!.entries) {
                jsonObject.put(entry.key, entry.value)
            }
            extra = jsonObject.toString()
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }
}