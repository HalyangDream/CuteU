package com.amigo.store

import android.view.LayoutInflater
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.widget.ProgressBar
import com.amigo.baselogic.deviceDataStore
import com.amigo.baselogic.userDataStore
import com.amigo.basic.util.StatusUtils
import com.amigo.pay.PAY_MODE_WEB
import com.amigo.pay.PayClient
import com.amigo.pay.PayResultCallback
import com.amigo.pay.PayResultJSInterface
import com.amigo.pay.WebviewPayActivity
import com.amigo.store.R
import com.amigo.tool.AppUtil
import com.amigo.tool.Toaster
import com.amigo.uibase.Constant
import com.amigo.uibase.userbehavior.UserBehavior
import org.json.JSONException
import org.json.JSONObject

/**
 * author : mac
 * date   : 2022/4/28
 *
 */
class NewWebPayActivity : WebviewPayActivity(), PayResultCallback {

    private var progressBar: ProgressBar? = null
    private var backView: View? = null
    private var source: String? = ""

    override fun addTopView(): View? {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_webpay_title, null)
        StatusUtils.setImmerseLayout(view, this)
        backView = view?.findViewById(R.id.btn_close)
        progressBar = view?.findViewById(R.id.mProgressBar)
        backView?.setOnClickListener {
            finish()
        }
        source = intent.getStringExtra("source")
        return view
    }

    override fun addJavaScriptInterface(): PayResultJSInterface? {
        return MyJSInterface(this)
    }

    override fun loadUrlProgress(progress: Int) {
        if (progress in 1..99) {
            progressBar?.visibility = View.VISIBLE
            progressBar?.progress = progress
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    override fun jsAlert(url: String?, message: String?, result: JsResult?) {
    }

    override fun onPaySuccess(payMethod: Int, orderNo: String?, extra: String?) {
        if (payMethod == PAY_MODE_WEB) {
            try {
                val jsonObject = JSONObject(extra)
                val data = jsonObject.optJSONObject("data")
                val productType = data.optInt("product_type")
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
            Toaster.showShort(this, "Pay Success")
        }
    }

    override fun onPayFail(payMethod: Int, orderNo: String?, extra: String?) {
        if (payMethod == PAY_MODE_WEB) {
            Toaster.showShort(this, "Pay Failure")
        }
    }

    override fun onPayCancel(payMethod: Int, orderNo: String?, extra: String?) {
        if (payMethod == PAY_MODE_WEB) {
            Toaster.showShort(this, "Pay Cancel")
        }
    }


    inner class MyJSInterface(activity: WebviewPayActivity) : PayResultJSInterface(activity) {


        @JavascriptInterface
        fun getToken(): String? {
            return userDataStore.readToken()
        }



        @JavascriptInterface
        fun getVersion(): String {
            return AppUtil.getAppVersion(this@NewWebPayActivity)
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
            return AppUtil.getAndroidID(this@NewWebPayActivity)
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