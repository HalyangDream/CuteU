package com.amigo.im

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.amigo.im.bean.Conversation
import com.amigo.im.bean.Msg
import com.amigo.im.bean.User
import com.amigo.im.dao.ConversationDao
import com.amigo.im.dao.MsgDao
import com.amigo.im.dao.UserDao


/**
 *  自动迁移
 *  autoMigrations = [
 *     AutoMigration (from = oldVersion, to = newVersion)
 *   ]
 *  autoMigrations = [AutoMigration(from = 1, to = 2)],
 */
@Database(
    entities = [Conversation::class, Msg::class, User::class],
    version = 1,
    exportSchema = true
)
abstract class IMDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun msgDao(): MsgDao

    abstract fun conversationDao(): ConversationDao
}