package com.amigo.im.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amigo.im.bean.Conversation
import com.amigo.im.bean.Msg


@Dao
interface MsgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msg: Msg): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(msg: Msg): Int

    @Delete
    fun delete(msg: Msg): Int

    @Query("DELETE FROM Message WHERE channel =:channel")
    fun deleteByChannel(channel: String): Int

    @Query("Select * from Message where channel =:channel order by timeStamp desc limit :count")
    fun query(count: Long, channel: String): List<Msg>?

    @Query("Select * from Message where channel =:anchorChannel and messageId!=:anchorMessageId and timeStamp<=:anchorTimestamp order by timeStamp desc limit :count ")
    fun queryMessageByAnchorCondition(
        count: Long,
        anchorChannel: String,
        anchorMessageId: String,
        anchorTimestamp: Long
    ): List<Msg>?

    @Query("Select * from Message where messageId =:messageId  limit 1")
    fun query(messageId: String): Msg?


    @Query("Select * from Message where receiveId =:userId  limit :count")
    fun queryMessageByReceive(count: Long, userId: String): List<Msg>?

    @Query("Select * from Message where channel =:channel  and mark=:messageType limit :count")
    fun queryMessageByMessageType(count: Long, channel: String, messageType: Int): List<Msg>?

}