package com.amigo.pay

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.lang.ref.WeakReference

/**
 * author : mac
 * date   : 2022/4/20
 *
 */
abstract class PayResultJSInterface constructor(activity: WebviewPayActivity) {

    private val weakReference = WeakReference<WebviewPayActivity>(activity)

    @JavascriptInterface
    fun onPayResult(json: String) {
        PayClient.get().handleWebPayResult(json)
    }

    @JavascriptInterface
    fun closeWebView() {
        weakReference.get()?.runOnUiThread {
            weakReference.get()?.finish()
        }
    }

    @JavascriptInterface
    fun registerExternalPayAppScheme(jsonArray: String?) {
        if (jsonArray.isNullOrEmpty()) return
        try {
            val jsonObjectArray = JSONArray(jsonArray)
            if (jsonObjectArray != null && jsonObjectArray.length() > 0) {
                val length = jsonObjectArray.length()
                for (i in 0 until length) {
                    val item = jsonObjectArray.optString(i)
                    weakReference.get()?.externalPayAppSchemes?.add(item)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}