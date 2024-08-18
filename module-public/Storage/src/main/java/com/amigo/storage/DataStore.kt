package com.amigo.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


abstract class DataStore {


    private lateinit var dataStore: DataStore<Preferences>


    fun initDataStore(context: Context, fileName: String) {
        dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ) {
            context.preferencesDataStoreFile(fileName)
        }
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    suspend fun putString(key: String, value: String) {
        dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }


    suspend fun putFloat(key: String, value: Float) {
        dataStore.edit {
            it[floatPreferencesKey(key)] = value
        }
    }

    suspend fun putDouble(key: String, value: Double) {
        dataStore.edit {
            it[doublePreferencesKey(key)] = value
        }
    }

    suspend fun putLong(key: String, value: Long) {
        dataStore.edit {
            it[longPreferencesKey(key)] = value
        }
    }

    suspend fun putStringSet(key: String, value: Set<String>) {
        dataStore.edit {
            it[stringSetPreferencesKey(key)] = value
        }
    }

    fun readInt(key: String, default: Int = 0): Int {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = intPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Int
            } else {
                default
            }
        }
    }


    fun readBoolean(key: String, default: Boolean = false): Boolean {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = booleanPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Boolean
            } else {
                default
            }
        }
    }

    fun readString(key: String, default: String = ""): String {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = stringPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as String
            } else {
                default
            }
        }
    }

    fun readDouble(key: String, default: Double = 0.0): Double {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = doublePreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Double
            } else {
                default
            }
        }
    }

    fun readFloat(key: String, default: Float = 0f): Float {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = floatPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Float
            } else {
                default
            }
        }
    }

    fun readLong(key: String, default: Long = 0L): Long {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = longPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Long
            } else {
                default
            }
        }
    }

    fun readStringSet(key: String): Set<String>? {
        return runBlocking {
            val preferences = dataStore.data.first()
            val preKey = stringSetPreferencesKey(key)
            if (preferences.contains(preKey)) {
                preferences[preKey] as Set<String>
            } else {
                null
            }
        }
    }

    fun clear() {
        runBlocking {
            dataStore.edit {
                it.clear()
            }
        }
    }

}