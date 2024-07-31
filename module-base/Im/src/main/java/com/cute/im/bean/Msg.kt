package com.cute.im.bean

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cute.im.cutom.CustomMessage
import org.json.JSONObject

@TypeConverters(MessageStatusConvert::class)
@Entity(
    tableName = "Message",
    indices = [Index(value = ["messageId", "channel"], unique = true)]
)
data class Msg(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var messageId: String,
    var channel: String,
    var timeStamp: Long = 0,
    var type: String,
    var mark: Int,
    var sendId: String,
    var receiveId: String,
    var originJson: String? = null,
    var status: MessageStatus = MessageStatus.SUCCESS,
) {
    @Ignore
    var message: CustomMessage? = null

    fun toJson(): String {
        try {
            val jsonObj = if (message != null) message!!.toJson()!! else JSONObject()
            jsonObj.put("type", type)
            jsonObj.put("mark", mark)
            jsonObj.put("message_id", messageId)
            jsonObj.put("channel_id", channel)
            jsonObj.put("sender_id", sendId)
            jsonObj.put("receiver_id", receiveId)
            jsonObj.put("time_stamp", timeStamp)
            return jsonObj.toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

}