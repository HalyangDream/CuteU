package com.amigo.analysis

import ai.datatower.analytics.DT
import ai.datatower.analytics.DTAnalytics
import ai.datatower.analytics.DTChannel
import ai.datatower.analytics.OnDataTowerIdListener
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
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
        FirebaseApp.initializeApp(/* context = */ context)
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

    fun getFirebaseId(context: Context, listener: ((String) -> Unit)?) {
        FirebaseAnalytics.getInstance(context).appInstanceId.addOnSuccessListener {
            if (!it.isNullOrEmpty()) {
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

    fun loginAccount(account: String, userPropJson: String?) {
        analytics?.setUserId(account)
        DTAnalytics.setAccountId(account);
        try {
            if (userPropJson.isNullOrEmpty()) return
            val jsonObject = JSONObject(userPropJson)
            DTAnalytics.userSet(jsonObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun track(eventName: String) {
        analytics?.logEvent(eventName, null)
        DTAnalytics.track(eventName, mutableMapOf<String, Any>().apply {
            put("app_id", appId)
        })
    }

    fun track(eventName: String, valueMap: Map<String, Any>) {
        try {
            val bundle = Bundle()
            val jsonObject = JSONObject()
            for (entry in valueMap.entries) {
                when (entry.value) {
                    is String -> bundle.putString(entry.key, entry.value as String)
                    is Int -> bundle.putInt(entry.key, entry.value as Int)
                    is Boolean -> bundle.putBoolean(entry.key, entry.value as Boolean)
                    is Long -> bundle.putLong(entry.key, entry.value as Long)
                    is Float -> bundle.putFloat(entry.key, entry.value as Float)
                    is Double -> bundle.putDouble(entry.key, entry.value as Double)
                    else -> bundle.putString(entry.key, "${entry.value}")
                }
                jsonObject.put(entry.key, entry.value)

            }
            jsonObject.put("app_id", appId)
            analytics?.logEvent(eventName, bundle)
            DTAnalytics.track(eventName, jsonObject)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun login() {
        analytics?.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    /**
     * 开始结账
     */
    fun beginCheckout(
        event: ProductEvent
    ) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, event.currency)
        if (isNumber(event.price)) {
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, event.price.toDouble())
        }
        val itemBoots = Bundle()
        if (isNumber(event.price)) {
            itemBoots.putDouble(FirebaseAnalytics.Param.PRICE, event.price.toDouble())
        }
        itemBoots.putString(FirebaseAnalytics.Param.ITEM_ID, event.sku)
        itemBoots.putString(FirebaseAnalytics.Param.ITEM_NAME, event.name)
        bundle.putParcelableArray(
            FirebaseAnalytics.Param.ITEMS,
            arrayOf<Parcelable>(itemBoots)
        )
        analytics?.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }


    /**
     * 支付成功
     */
    fun purchase(
        event: PurchaseEvent
    ) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, event.orderNo)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, event.price)
        val itemBoots = Bundle()
        itemBoots.putString(FirebaseAnalytics.Param.ITEM_ID, event.sku)
        itemBoots.putString(FirebaseAnalytics.Param.ITEM_NAME, event.name)
        bundle.putParcelableArray(
            FirebaseAnalytics.Param.ITEMS,
            arrayOf<Parcelable>(itemBoots)
        )
        analytics?.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
    }



    private fun isNumber(value: String): Boolean {
        return try {
            value.toBigDecimal()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

}