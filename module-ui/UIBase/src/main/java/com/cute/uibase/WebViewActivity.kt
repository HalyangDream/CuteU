package com.cute.uibase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.cute.baselogic.deviceDataStore
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseActivity
import com.cute.basic.util.StatusUtils
import com.cute.pay.PayResultJSInterface
import com.cute.pay.WebviewPayActivity
import com.cute.tool.AppUtil
import com.cute.uibase.databinding.ActivityWebviewBinding
import com.cute.uibase.databinding.LayoutTitleBarBinding
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.userbehavior.UserBehavior
import java.lang.ref.WeakReference

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    private lateinit var titleBarBinding: LayoutTitleBarBinding

    companion object {

        fun startWebView(context: Context, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityWebviewBinding {

        return ActivityWebviewBinding.inflate(layout)
    }

    override fun initView() {
        titleBarBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBarBinding.flTitle, this)
        titleBarBinding.ivNavBack.setOnClickListener {
            onBackPressed()
        }
        initWebViewSetting()
        val url = intent.getStringExtra("url")!!
        viewBinding.webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (viewBinding.webView.canGoBack()) {
            viewBinding.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.webView.destroy()
    }


    private fun initWebViewSetting() {
        val settings = viewBinding.webView.settings
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setSupportZoom(true)
        settings.allowFileAccess = true
        settings.blockNetworkImage = false //解决图片不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.builtInZoomControls = false
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadsImagesAutomatically = true // 支持自动加载图片
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        settings.loadWithOverviewMode = true
        viewBinding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        viewBinding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!TextUtils.isEmpty(view.title)) {
                    titleBarBinding.tvTitle.text = view.title
                }
            }
        }
        viewBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    viewBinding.progressBar.setProgress(newProgress + 10, true)
                } else {
                    viewBinding.progressBar.progress = newProgress + 10
                }
                if (newProgress >= 100) {
                    viewBinding.progressBar.visibility = View.GONE
                }
            }
        }
        viewBinding.webView.addJavascriptInterface(MyJSInterface(this), "Bak")
    }


    inner class MyJSInterface(activity: Activity) {

        private val weakReference = WeakReference(activity)

        @JavascriptInterface
        fun showChargePage(popCode: String) {
            runOnUiThread {
                val iStoreService = RouteSdk.findService(IStoreService::class.java)
                iStoreService.showCodeDialog(popCode, null)
            }
        }

        @JavascriptInterface
        fun navigationUserDetail(userId: Long) {
            runOnUiThread {
                RouteSdk.navigationUserDetail(userId, "webView")
            }
        }

        @JavascriptInterface
        fun navigationChat(userId: Long) {
            runOnUiThread {
                RouteSdk.navigationChat(userId, "webView")
            }
        }



        @JavascriptInterface
        fun getToken(): String? {
            return userDataStore.readToken()
        }


        @JavascriptInterface
        fun getVersion(): String {
            return AppUtil.getAppVersion(this@WebViewActivity)
        }

        //
        @JavascriptInterface
        fun getOS(): String? {
            return "Android"
        }

        @JavascriptInterface
        fun getOvers(): String? {
            return AppUtil.getOSVersion()
        }

        //
        @JavascriptInterface
        fun getMake(): String? {
            return AppUtil.getOSBrand()
        }

        @JavascriptInterface
        fun getModel(): String? {
            return AppUtil.getOSModel()
        }


        @JavascriptInterface
        fun getAfId(): String? {
            return ""
        }

        @JavascriptInterface
        fun getThirdPartyId(): String? {
            return deviceDataStore.getThirdPartyId()
        }

        @JavascriptInterface
        fun getAdId(): String? {
            return deviceDataStore.getAdId()
        }

        @JavascriptInterface
        fun getUtmSource(): String? {
            return deviceDataStore.getReferrer()
        }

        @JavascriptInterface
        fun getAndroidId(): String? {
            return AppUtil.getAndroidID(this@WebViewActivity)
        }

        @JavascriptInterface
        fun getPageLocation(): String? {
            return UserBehavior.chargeSource
        }

        @JavascriptInterface
        fun getPayEventSource(): String? {
            return UserBehavior.root
        }
    }
}