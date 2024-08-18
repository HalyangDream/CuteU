package com.amigo.pay

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import com.android.billingclient.api.BillingClient
import com.amigo.pay.annotation.PayMode
import com.amigo.pay.annotation.PayResult
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.*

/**
 * author : mac
 * date   : 2022/4/20
 *
 */

const val PAY_SUCCESS = 1
const val PAY_FAIL = 2
const val PAY_CANCEL = 3

class PayClient private constructor() {


    private val orderNos by lazy { mutableListOf<String?>() }

    private val listeners by lazy { mutableListOf<WeakReference<PayResultCallback?>>() }

    private val payScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private val intance by lazy { PayClient() }
        fun get(): PayClient {
            return intance
        }
    }


    /**
     * 初始化Google支付
     */
    private fun initGooglePay(context: Context) {
        GooglePayClient.initialize(context) {
        }
    }

    /**
     * billing 服务不可用dialog提示
     *
     * @param activity
     */
    private fun showBillingUnavailableDialog(activity: Activity?) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) return
        try {
            val dialog = AlertDialog.Builder(activity).setTitle("In-App Billing Setup Failed")
                .setMessage("Failed establish a connection with the In-App Billing service on Google Play. Please make sure network or you have logged into Google Play with your Google Account or you can try later.")
                .setPositiveButton("OK", null)
                .create()
            dialog.show()
            val window = dialog.window
            window?.setGravity(Gravity.CENTER)
            val d = window?.windowManager?.defaultDisplay
            val p = window?.attributes
            val width = d?.width?.times(0.88)
            width?.let {
                p?.width = it.toInt()
            }
            window?.attributes = p
        } catch (ex: WindowManager.BadTokenException) {
            ex.printStackTrace()
        }
    }

    /**
     * 启动一个Google支付
     */
    private fun launchGooglePay(activity: Activity, pay: Pay) {

        payScope.launch(Dispatchers.IO) {
            if (!GooglePayClient.isBillingReady()) {
                initGooglePay(activity)
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                if (!GooglePayClient.isBillingReady()) {
                    handleGoogleResult(PAY_FAIL, "", "Google Play Not available")
                    showBillingUnavailableDialog(activity)
                    return@withContext
                }
                GooglePayClient.querySkuDetail(
                    pay.isSub,
                    mutableListOf(pay.skuId)
                ) { code, skuDetailsList ->
                    if (code == BillingClient.BillingResponseCode.OK
                        && !skuDetailsList.isNullOrEmpty()
                    ) {
                        val result = GooglePayClient.launchBillingFlow(
                            pay.userId,
                            pay.orderNo,
                            activity,
                            skuDetailsList[0]
                        )
                        if (result != BillingClient.BillingResponseCode.OK) {
                            handleGoogleResult(
                                PAY_FAIL,
                                "",
                                "Pay failure:$code,result=${result},sku=${pay.skuId},product:${skuDetailsList?.size}"
                            )
                        }

                    } else {
                        //启动支付失败
                        //注意：这里可能会出现查询不到商品的情况，所以需要做一个异常处理
                        handleGoogleResult(
                            PAY_FAIL,
                            "",
                            "Sku not exist:$code,sku=${pay.skuId},product:${skuDetailsList?.size}"
                        )
                    }
                }
            }
        }


    }

    /**
     * 启动一个Web支付
     */
    private fun launchWebPay(
        activity: Activity,
        pay: Pay,
        webViewActivity: Class<out WebviewPayActivity>?
    ) {
        if (webViewActivity != null) {
            val intent = Intent(activity, webViewActivity)
            intent.putExtra("pay_url", pay.webPayUrl)
            intent.putExtra("source", pay.source)
            activity.startActivity(intent)
        } else {
            val json = JSONObject()
            json.put("result", "fail")
            handleWebPayResult(json.toString())
        }
    }

    /**
     * 启动一个PayTm支付
     */
//    private fun launchPayTmPay(activity: Activity, pay: Pay) {
//        val paytmCode = PayUtils.getPaytmVersion(activity)
//        val high = PayUtils.paytmApp(paytmCode)
//        // paytm app
//        val uri = Uri.parse(pay.intentUrl)
//        val amount = uri.getQueryParameter("amount")
//        val orderId = uri.getQueryParameter("orderId")
//        val txnToken = uri.getQueryParameter("txnToken")
//        val mid = uri.getQueryParameter("mid")
//        val result = if (high) {
//            PayUtils.goHigher860Paytm(activity, amount, orderId, txnToken, mid)
//        } else {
//            PayUtils.goLess860Paytm(activity, amount, orderId, txnToken, mid)
//        }
//        if (!result) {
//            //启动支付失败
//            val intent = Intent()
//            intent.putExtra("nativeSdkForMerchantMessage", "Not Launch PayTm")
//            handlePayTmResult(PayUtils.PAYTM_PAT_REQUEST_CODE, intent)
//        }
//    }

    /**
     * 启动UPI支付
     */
//    private fun launchUPIPay(activity: Activity, pay: Pay) {
//        val result = PayUtils.launchUPIPay(activity, pay.intentUrl)
//        if (!result) {
//            //启动支付失败
//            val intent = Intent()
//            intent.putExtra("response", "Not Launch UPI")
//            handleUPIResult(PayUtils.UPI_PAT_REQUEST_CODE, intent)
//        }
//    }

    /**
     * 添加支付回调
     */
    private fun addPayResultListener(listener: PayResultCallback?) {
        if (listener == null) return
        synchronized(listeners) {
            var add = true
            for (weakReference in listeners) {
                val item = weakReference.get()
                if (item != null && item === listener) {
                    add = false
                }
            }
            if (add) {
                listeners.add(WeakReference(listener))
            }
        }
    }

    private fun getLastOrderNo(): String? {
        synchronized(orderNos) {
            val size = orderNos.size
            if (size > 0) {
                return orderNos[size - 1]
            }
            return ""
        }
    }


    fun notifyPayResult(
        @PayResult payResult: Int, @PayMode payModel: Int,
        orderNo: String?, extra: String?
    ) {
        synchronized(listeners) {
            for (listener in listeners) {
                val item = listener.get()
                when (payResult) {
                    PAY_SUCCESS -> item?.onPaySuccess(payModel, orderNo, extra)
                    PAY_FAIL -> item?.onPayFail(payModel, orderNo, extra)
                    PAY_CANCEL -> item?.onPayCancel(payModel, orderNo, extra)
                }
            }
        }
    }


    /**
     * 启动支付
     */
    fun launchPay(
        activity: Activity,
        pay: Pay,
        listener: PayResultCallback?,
        webViewActivity: Class<out WebviewPayActivity>? = null
    ) {
        synchronized(orderNos) {
            orderNos.clear()
            orderNos.add(pay.orderNo)
        }
        addPayResultListener(listener)
        when (pay.pay) {
            PAY_MODE_GOOGLE -> launchGooglePay(activity, pay)
//            PAY_MODE_PAYTM -> launchPayTmPay(activity, pay)
//            PAY_MODE_UPI -> launchUPIPay(activity, pay)
            PAY_MODE_WEB -> launchWebPay(activity, pay, webViewActivity)
            else -> throw IllegalArgumentException("无效的支付方式")
        }
    }


    fun handleGoogleResult(@PayResult payResult: Int, orderNo: String?, extra: String?) {
        val order_no = if (orderNo.isNullOrEmpty()) getLastOrderNo() else orderNo
        notifyPayResult(payResult, PAY_MODE_GOOGLE, order_no, extra)
    }

    /**
     * 处理UPI的支付结果
     */
//    fun handleUPIResult(requestCode: Int, data: Intent?) {
//        if (requestCode != PayUtils.UPI_PAT_REQUEST_CODE || data == null) return
//        val response = data.getStringExtra("response")
//        val success = response?.lowercase(Locale.ENGLISH)?.contains("success") ?: false
//        if (success) {
//            notifyPayResult(PAY_SUCCESS, PAY_MODE_UPI, getLastOrderNo(), response)
//        } else {
//            val extra = if (response.isNullOrEmpty()) "Pay Error" else response
//            notifyPayResult(PAY_FAIL, PAY_MODE_UPI, getLastOrderNo(), extra)
//        }
//    }

    /**
     * 处理PayTm的支付结果
     */
//    fun handlePayTmResult(requestCode: Int, data: Intent?) {
//        if (requestCode != PayUtils.PAYTM_PAT_REQUEST_CODE || data == null) return
//        try {
//            val response = data.getStringExtra("response")
//            if (response.isNullOrEmpty()) {
//                val jsonObject = JSONObject(response)
//                val status = jsonObject.getString("STATUS")
//                val orderId = jsonObject.getString("ORDERID")
//                if (!status.isNullOrEmpty() && status.equals("TXN_SUCCESS")) {
//                    notifyPayResult(PAY_SUCCESS, PAY_MODE_PAYTM, orderId, response)
//                } else {
//                    val message = data.getStringExtra("nativeSdkForMerchantMessage")
//                    val extra = if (message.isNullOrEmpty()) "Pay Error" else message
//                    notifyPayResult(PAY_FAIL, PAY_MODE_PAYTM, getLastOrderNo(), extra)
//                }
//            } else {
//                val message = data.getStringExtra("nativeSdkForMerchantMessage")
//                val extra = if (message.isNullOrEmpty()) "Pay Error" else message
//                notifyPayResult(PAY_FAIL, PAY_MODE_PAYTM, getLastOrderNo(), extra)
//            }
//
//        } catch (ex: JSONException) {
//            ex.printStackTrace()
//            notifyPayResult(PAY_FAIL, PAY_MODE_PAYTM, getLastOrderNo(), "Parse Result Error")
//        }
//    }

    fun handleWebPayResult(json: String?) {
        try {
            val jsonObject = JSONObject(json)
            val result = jsonObject.optString("result")
            when (result) {
                "success" -> {
                    val data = jsonObject.optJSONObject("data")
                    val productType = data.optInt("product_type")
                    val price = data.optString("price")
                    val payment = data.optString("payment")
                    val order = data.optString("order_no")
                    notifyPayResult(PAY_SUCCESS, PAY_MODE_WEB, order, json)
                }

                "fail" -> {
                    notifyPayResult(PAY_FAIL, PAY_MODE_WEB, getLastOrderNo(), json)
                }

                "pedding" -> {
                    notifyPayResult(PAY_CANCEL, PAY_MODE_WEB, getLastOrderNo(), json)
                }
            }
        } catch (exception: JSONException) {
            exception.printStackTrace()
            notifyPayResult(PAY_FAIL, PAY_MODE_WEB, getLastOrderNo(), "Parse Result Error")
        }
    }


    fun queryGooglePurchase(
        context: Context,
        block: (purchaseToken: String?, purchaseOrderId: String?, originJson: String?) -> Unit
    ) {

        payScope.launch(Dispatchers.IO) {
            if (!GooglePayClient.isBillingReady()) {
                initGooglePay(context)
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                if (GooglePayClient.isBillingReady()) {
                    GooglePayClient.queryInAppPurchases { purchaseToken, purchaseOrderId, originJson ->
                        block(purchaseToken, purchaseOrderId, originJson)
                    }
                }
            }
        }

    }
}