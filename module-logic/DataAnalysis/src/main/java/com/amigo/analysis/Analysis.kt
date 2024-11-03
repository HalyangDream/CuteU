package com.amigo.analysis

import ai.datatower.analytics.DT
import ai.datatower.analytics.DTAnalytics
import ai.datatower.analytics.DTChannel
import ai.datatower.analytics.OnDataTowerIdListener
import android.content.Context
import android.os.Bundle
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import org.json.JSONObject

object Analysis {

    private var analytics: FirebaseAnalytics? = null
    private var appId: Int = 0
    fun initAnalysis(
        context: Context, appId: Int, dtAppId: String, dtServerUrl: String, isDebug: Boolean
    ) {
        this.appId = appId
        FirebaseApp.initializeApp(context)
        analytics = FirebaseAnalytics.getInstance(context)
        DT.initSDK(context, dtAppId, dtServerUrl, DTChannel.GP, isDebug)
    }

    fun getDtId(listener: ((String) -> Unit)?) {
        DTAnalytics.getDataTowerId(object : OnDataTowerIdListener {
            override fun onDataTowerIdCompleted(dataTowerId: String) {
                listener?.invoke(dataTowerId)
            }
        })
    }

    fun getFirebaseId(context: Context,listener: ((String) -> Unit)?){
        FirebaseAnalytics.getInstance(context).appInstanceId.addOnSuccessListener {
          if(!it.isNullOrEmpty()){
              DTAnalytics.setFirebaseAppInstanceId(it);
              listener?.invoke(it)
          }
        }
    }

    fun getGoogleAdId(context: Context): String? {
        return try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun loginAccount(account: String) {
        analytics?.setUserId(account)
        DTAnalytics.setAccountId(account);
    }

//    fun track(eventName: String) {
//        analytics?.logEvent(eventName, null)
//        DTAnalytics.track(eventName, mutableMapOf<String, Any>().apply {
//            put("app_id", appId)
//        })
//    }
//
//    fun track(eventName: String, valueMap: Map<String, Any>) {
//        try {
//            val bundle = Bundle()
//            val jsonObject = JSONObject()
//            for (entry in valueMap.entries) {
//                when (entry.value) {
//                    is String -> bundle.putString(entry.key, entry.value as String)
//                    is Int -> bundle.putInt(entry.key, entry.value as Int)
//                    is Boolean -> bundle.putBoolean(entry.key, entry.value as Boolean)
//                    is Long -> bundle.putLong(entry.key, entry.value as Long)
//                    is Float -> bundle.putFloat(entry.key, entry.value as Float)
//                    is Double -> bundle.putDouble(entry.key, entry.value as Double)
//                    else -> bundle.putString(entry.key, "${entry.value}")
//                }
//                jsonObject.put(entry.key, entry.value)
//
//            }
//            jsonObject.put("app_id", appId)
//            analytics?.logEvent(eventName, bundle)
//            DTAnalytics.track(eventName, jsonObject)
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }

}