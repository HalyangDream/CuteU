package com.amigo.uibase

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import kotlin.properties.Delegates


object Constant {

    lateinit var PRIVACY_AGREEMENT: String
    lateinit var USER_AGREEMENT: String

//    fun orderListUrl(token: String): String {
//        return "${WEB_URL}/v1/payment/orders?" +
//                "app_id=${APP_ID}&app_key=${APP_KEY}&token=${token}"
//    }
//
//
//     fun configRedirect(
//        context: Context,
//        source: String,
//        type: String,
//        config: RedirectConfig
//    ) {
//        when (type) {
//            "game" -> {
//                val url = config.url
//                if (!url.isNullOrEmpty()) {
//                    GameWebViewActivity.startWebView(context, url)
//                }
//            }
//
//            "web" -> {
//                val url = config.url
//                if (!url.isNullOrEmpty()) {
//                    WebViewActivity.startWebView(context, url)
//                }
//            }
//
//            "browser" -> {
//                val url = config.url
//                if (!url.isNullOrEmpty()) {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.setData(Uri.parse(url)) // Replace with the URL you want to open
//                    context.startActivity(intent)
//                }
//            }
//
//            "dialog" -> {
//                val popCode = config.popCode
//                if (!popCode.isNullOrEmpty()) {
//                    val iStoreService = RouteSdk.findService(IStoreService::class.java)
//                    iStoreService.showCodeDialog(popCode, null)
//                }
//            }
//
//            "native" -> {
//                val nativeContent = config.nativeContent ?: return
//                val uiPage = nativeContent.uiPage
//                val userId = nativeContent.userId
//                if (uiPage == "chat") {
//                    RouteSdk.navigationChat(userId, source)
//                    return
//                }
//                if (uiPage == "anchor") {
//                    RouteSdk.navigationUserDetail(userId, source)
//                    return
//                }
//            }
//        }
//    }


}