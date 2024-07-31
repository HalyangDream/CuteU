package com.cute.im.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cute.im.bean.Conversation


@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(conversation: Conversation): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(conversation: Conversation): Int

    @Delete
    fun delete(conversation: Conversation): Int

    @Query("DELETE from Conversation where channel =:channel")
    fun deleteByChannel(channel: String): Int

    @Query("Select * from Conversation where channel =:channel")
    fun query(channel: String): Conversation?


    @Query("Select * from Conversation where uid=:self order by timeStamp desc")
    fun queryBySelf(self: String): List<Conversation>?

    @Query("Select * from Conversation where uid=:self order by timeStamp desc limit :count")
    fun queryBySelf(count: Long, self: String): MutableList<Conversation>?


    @Query("Select * from Conversation where peer=:peer order by timeStamp desc")
    fun queryByPeer(peer: String): List<Conversation>?

    @Query("Select * from Conversation where channel!=:anchorChannel and uid = :uid and timeStamp<=:anchorTimestamp order by timeStamp desc limit :count")
    fun queryByAnchorCondition(
        count: Long,
        uid: String,
        anchorChannel: String,
        anchorTimestamp: Long
    ): MutableList<Conversation>?


    @Query("Select * from Conversation where channel in (:channels)")
    fun queryInChannel(
        channels: List<String>
    ): List<Conversation>?

    @Query("Select * from Conversation where uid =:uid and peer in (:peers)")
    fun queryInPeer(
        uid: String,
        peers: List<String>
    ): List<Conversation>?

    @Query("Select SUM(unreadCount) from Conversation where uid =:uid")
    fun queryUnReadCount(uid: String): Int


    @Query("Update Conversation set unreadCount =0 where channel=:channel")
    fun clearUnReadCountForChannel(channel: String): Int

    @Query("Update Conversation set unreadCount =0 where uid=:uid")
    fun clearUnReadCountForUid(uid: String): Int

}