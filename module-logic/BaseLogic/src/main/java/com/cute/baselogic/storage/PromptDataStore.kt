package com.cute.baselogic.storage

import android.content.Context
import android.util.TimeUtils
import com.cute.storage.DataStore
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

class PromptDataStore private constructor(context: Context) : DataStore() {

    companion object {

        private var instance: PromptDataStore? = null

        fun get(context: Context): PromptDataStore {
            if (instance == null) {
                synchronized(UserDataStore::class.java) {
                    if (null == instance) {
                        instance = PromptDataStore(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        initDataStore(context, "prompt-preference")
    }


    /**
     * 提示通知栏权限
     */
    fun promptNotifyPermission(): Boolean {
        val date = readString("notify_permission", "")
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val curTimeStamp = System.currentTimeMillis()
        val curDate = simpleDateFormat.format(curTimeStamp)
        if (curDate != date) {
            runBlocking { putString("notify_permission", curDate) }
            return true
        }
        return false
    }


}