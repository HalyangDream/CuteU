package com.amigo.im.bean

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id"], unique = true), Index(value = ["channel"], unique = true)])
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var channel: String,
    var uid: String,
    var peer: String,
    var unreadCount: Int = 0,
    var messageType: Int,
    var timeStamp: Long = 0,
    var extra: String? = null,
    var lastMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Conversation) return false
        return this.channel == other.channel
    }
}