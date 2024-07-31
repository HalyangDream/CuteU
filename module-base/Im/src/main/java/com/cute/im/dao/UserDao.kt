package com.cute.im.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cute.im.bean.User

@Dao
interface UserDao {

    @Query("select * from User where uid =:uid")
    fun queryUser(uid: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(user: User): Int

    @Delete
    fun delete(user: User): Int
}