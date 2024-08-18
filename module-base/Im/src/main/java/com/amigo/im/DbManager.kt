package com.amigo.im

import android.content.Context
import androidx.room.Room
import java.lang.IllegalArgumentException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DbManager private constructor() {


    companion object {

        private val instance by lazy { DbManager() }

        fun get(): DbManager {
            return instance
        }

    }

    private lateinit var db: IMDatabase
    private val cacheMap = HashMap<Class<*>, Any>()

    fun initDb(context: Context) {
        db = Room.databaseBuilder(context, IMDatabase::class.java, "IM_Database.db")
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
            .setTransactionExecutor(dbThreadPool)
            .allowMainThreadQueries()
            .build()
    }


    fun <T> getDao(clazz: Class<T>): T {
        val result = cacheMap.contains(clazz)
        if (result) {
            return cacheMap[clazz] as T
        }

        val name = "${clazz.name}_Impl"
        if (db.userDao().javaClass.name == name) {
            val dao = db.userDao()
            cacheMap[clazz] = dao
            return dao as T
        }

        if (db.msgDao().javaClass.name == name) {
            val dao = db.msgDao()
            cacheMap[clazz] = dao
            return dao as T
        }

        if (db.conversationDao().javaClass.name == name) {
            val dao = db.conversationDao()
            cacheMap[clazz] = dao
            return dao as T
        }

        throw IllegalArgumentException("DbManager中 ${clazz.name} 没有实现")
    }


    private val dbThreadPool =
        ThreadPoolExecutor(
            2,
            10,
            60,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            DbThreadFactory(), CallerRunsPolicy()
        )

    private inner class DbThreadFactory : ThreadFactory {

        private val THREAD_NUM = AtomicInteger(1)

        override fun newThread(r: Runnable?): Thread {
            val name = "Room-Thread-${THREAD_NUM.getAndIncrement()}"
            val thread = Thread(r, name)
            if (thread.isDaemon) {
                thread.isDaemon = false
            }
            return thread
        }
    }

}