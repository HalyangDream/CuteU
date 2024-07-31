package com.cute.baselogic.storage

import android.content.Context
import com.cute.storage.DataStore
import kotlinx.coroutines.runBlocking

class StatusDataStore private constructor(context: Context) : DataStore() {

    companion object {

        private var instance: StatusDataStore? = null

        fun get(context: Context): StatusDataStore {
            if (instance == null) {
                synchronized(UserDataStore::class.java) {
                    if (null == instance) {
                        instance = StatusDataStore(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        initDataStore(context, "status-preference")
    }



    fun hasAgreeUgc(): Boolean {
        return readBoolean("agree-ugc", false)
    }

    fun saveAgreeUgc(value: Boolean) {
        runBlocking { putBoolean("agree-ugc", value) }
    }


    /**
     * 默认没有关闭摄像头
     */
    fun saveCloseCamera(isCloseCamera: Boolean) {
        runBlocking { putBoolean("close-camera", isCloseCamera) }
    }

    fun hasCloseCamera(): Boolean {
        return readBoolean("close-camera", false)
    }

    /**
     * 默认使用前置摄像头
     */
    fun saveUseFrontCamera(isUseFrontCamera: Boolean) {
        runBlocking { putBoolean("use-front-camera", isUseFrontCamera) }
    }

    fun hasUseFrontCamera(): Boolean {
        return readBoolean("use-front-camera", true)
    }

    /**
     * 默认不可以静音
     */
    fun saveMuteVoice(muteVoice: Boolean) {
        runBlocking { putBoolean("mute-voice", muteVoice) }
    }

    fun hasMuteVoice(): Boolean {
        return readBoolean("mute-voice", false)
    }
}