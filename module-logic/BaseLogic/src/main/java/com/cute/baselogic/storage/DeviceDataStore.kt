package com.cute.baselogic.storage

import android.content.Context
import com.cute.storage.DataStore
import kotlinx.coroutines.runBlocking

class DeviceDataStore private constructor(context: Context) : DataStore() {

    companion object {

        private var instance: DeviceDataStore? = null

        fun get(context: Context): DeviceDataStore {
            if (instance == null) {
                synchronized(UserDataStore::class.java) {
                    if (null == instance) {
                        instance = DeviceDataStore(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        initDataStore(context, "devices-preference")
    }


    fun saveReferrer(referrer: String) {
        runBlocking { putString("referrer", referrer) }
    }

    fun getReferrer(): String {
        return readString("referrer", "")
    }

    fun saveAdId(adId: String) {
        runBlocking { putString("adId", adId) }
    }

    fun getAdId(): String {
        return readString("adId", "")
    }

    fun saveThirdPartyId(thirdPartyId: String) {
        runBlocking { putString("thirdPartyId", thirdPartyId) }
    }

    fun getThirdPartyId(): String {
        return readString("thirdPartyId", "")
    }


    fun saveSplashIds(placementIds: Set<String>) {
        runBlocking { putStringSet("adSplashIds", placementIds) }
    }

    fun readSplashIds(): Set<String>? {
        return readStringSet("adSplashIds")
    }

}