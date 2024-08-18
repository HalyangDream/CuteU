package com.amigo.im.bean

import androidx.room.TypeConverter

class MessageStatusConvert {


    @TypeConverter
    fun statusToInt(status: MessageStatus): Int {
        return status.status
    }

    @TypeConverter
    fun intToStatus(status: Int): MessageStatus {
        for (value in MessageStatus.values()) {
            if (value.status == status) {
                return value
            }
        }
        return MessageStatus.FAIL
    }
}