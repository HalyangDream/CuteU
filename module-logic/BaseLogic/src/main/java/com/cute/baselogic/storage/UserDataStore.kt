package com.cute.baselogic.storage

import android.content.Context
import com.cute.storage.DataStore
import kotlinx.coroutines.runBlocking

class UserDataStore private constructor(context: Context) : DataStore() {


    companion object {

        private var instance: UserDataStore? = null

        fun get(context: Context): UserDataStore {
            if (instance == null) {
                synchronized(UserDataStore::class.java) {
                    if (null == instance) {
                        instance = UserDataStore(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        initDataStore(context, "user-preference")
    }

    fun saveUid(uid: Long) {
        runBlocking { putLong("uid", uid) }
    }

    fun getUid(): Long {
        return readLong("uid", 0)
    }

    fun saveOfficialAccount(officialAccount: String) {
        runBlocking { putString("official_account", officialAccount) }
    }

    fun getOfficialAccount(): String {
        return readString("official_account", "")
    }

    fun saveToken(token: String) {
        runBlocking { putString("token", token) }
    }

    fun readToken(): String {
        return readString("token", "")
    }

    fun saveAvatar(avatar: String) {
        runBlocking { putString("avatar", avatar) }
    }

    fun readAvatar(): String {
        return readString("avatar", "")
    }

    fun saveVip(isVip: Boolean) {
        runBlocking { putBoolean("vip", isVip) }
    }

    fun readVip(): Boolean {
        return readBoolean("vip", false)
    }

    fun role(): String {
        return readString("role", "1")
    }

    fun saveRole(role: String) {
        runBlocking { putString("role", role) }
    }

    fun saveCoinMode(coinMode: Boolean) {
        runBlocking { putBoolean("coin_mode", coinMode) }
    }

    fun hasCoinMode(): Boolean {
        return runBlocking { readBoolean("coin_mode", false) }
    }

    fun saveLikeMeUnReadCount(unReadCount: Int) {
        runBlocking { putInt("like_me_unread_count", unReadCount) }
    }

    fun getLikeMeUnReadCount(): Int {
        return readInt("like_me_unread_count", 0)
    }

}

