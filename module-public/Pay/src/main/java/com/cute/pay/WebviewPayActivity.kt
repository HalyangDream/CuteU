package com.cute.pay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import java.lang.reflect.InvocationTargetException

/**
 * author : mac
 * date   : 2022/4/28
 *
 */
abstract class WebviewPayActivity : Activity() {

    private val linearLayout by lazy { LinearLayout(this) }
    private val javascriptInterfaceName = "Bak"
    private val webView by lazy { WebView(this) }
    val externalPayAppSchemes by lazy { mutableListOf<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(generateViewGroup())
        addChildUI()
        initData()
    }


    private fun generateViewGroup(): View {
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        linearLayout.layoutParams = layoutParams
        linearLayout.orientation = LinearLayout.VERTICAL
        return linearLayout
    }

    private fun addChildUI() {
        val linearLayoutParams = LinearLayout.LayoutParams(-1, -1)
        webView.layoutParams = linearLayoutParams
        val topView = addTopView()
        if (topView != null) {
            linearLayout.addView(topView)
        }
        linearLayout.addView(webView)
        setWebviewSetting()
    }

    private fun setWebviewSetting() {
        val webSettings = webView.settings
//        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true)
        webSettings.setLoadWithOverviewMode(true)

        // mWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTheme)); // 设置背景色
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true)
        //支持插件
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //设置自适应屏幕，两者合用  这样会使加载文本时 文字变小
        //webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        //webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染的优先级
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //不使用缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //这个是加载的地址是https的，一些资源文件使用的是http方法的，
        // 从安卓4.4之后对webview安全机制有了加强，webview里面加载https url的时候，
        // 如果里面需要加载http的资源或者重定向的时候，webview会block页面加载。需要设置MixedContentMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                loadUrlProgress(newProgress)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                val message = consoleMessage?.message()
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                jsAlert(url, message, result)
                return super.onJsAlert(view, url, message, result)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                loadUrlProgress(100)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (isSupportExternalAppPay(url)) {
                    openExternalAppPay(url)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                if (isSupportExternalAppPay(url)) {
                    openExternalAppPay(url)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }


        var jsInterface = addJavaScriptInterface()
        if (jsInterface == null) {
            jsInterface = object : PayResultJSInterface(this) {}
        }
        webView.addJavascriptInterface(jsInterface, javascriptInterfaceName)
    }

    private fun initData() {
        val pay_url = intent.getStringExtra("pay_url")
        if (!pay_url.isNullOrEmpty()) {
            webView.loadUrl(pay_url)
        }
    }

    private fun isSupportExternalAppPay(url: String?): Boolean {
        if (url.isNullOrEmpty()) return false
        if (externalPayAppSchemes.isNullOrEmpty()) return false
        for (externalPayAppScheme in externalPayAppSchemes) {
            if (url.startsWith(externalPayAppScheme)) return true
        }
        return false
    }

    private fun openExternalAppPay(scheme: String?) {
        val result = try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            startActivity(intent)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, "No related app found", Toast.LENGTH_LONG).show()
            false
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            webView.javaClass.getMethod("onResume").invoke(webView, null)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            webView.javaClass.getMethod("onPause").invoke(webView, null)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    override fun finish() {
        webView.stopLoading()
        webView.stopLoading()
        webView.destroyDrawingCache()
        webView.removeAllViews()
        webView.clearHistory()
        webView.destroy()
        linearLayout.removeAllViews()
        super.finish()
    }

    /**
     * 按键响应，在WebView中查看网页时，检查是否有可以前进的历史记录。
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            // 返回键退回
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    abstract fun addTopView(): View?

    abstract fun addJavaScriptInterface(): PayResultJSInterface?

    abstract fun loadUrlProgress(progress: Int)

    abstract fun jsAlert(
        url: String?,
        message: String?,
        result: JsResult?
    )


}