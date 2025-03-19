package com.amigo.store

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.amigo.analysis.Analysis
import com.amigo.analysis.ProductEvent
import com.amigo.baselogic.userDataStore
import com.amigo.logic.http.model.PayRepository
import com.amigo.logic.http.response.pay.Order
import com.amigo.logic.http.response.pay.Payment
import com.amigo.logic.http.response.product.Product
import com.amigo.pay.PAY_MODE_GOOGLE
import com.amigo.pay.Pay
import com.amigo.pay.PayClient
import com.amigo.pay.PayResultCallback
import com.amigo.tool.EventBus
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.Constant
import com.amigo.uibase.DefaultLoadingDialog
import com.amigo.uibase.event.PayResultEvent
import com.amigo.uibase.userbehavior.UserBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object PayViewModel : PayResultCallback {
    private val dialog = DefaultLoadingDialog()
    private val payRepository = PayRepository()
    private val googlePay =
        Payment(1, "GP Pay", img = "", type = "native")

    private val payScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    private var payResult: ((result: Boolean, msg: String) -> Unit)? = null

    /**
     * 启动支付收银台
     */
    fun launchSettlementStore(
        activity: FragmentActivity?,
        product: Product,
        fromPopCode: String,
        block: ((result: Boolean, msg: String) -> Unit)?
    ) {
        Analysis.beginCheckout(
            ProductEvent(
                product.google,
                product.name,
                "USD",
                product.googlePrice
            )
        )
        if (activity == null) return
        this.payResult = block
        dialog.showDialog(activity, null)
        if (activity.userDataStore.role() == "4") {
            launchPay(activity, fromPopCode, product, googlePay, block)
            dialog.dismissDialog()
            Analysis.track("payment_channel_click_pay", mutableMapOf<String, Any>().apply {
                put("code", fromPopCode)
                put("source", UserBehavior.root)
                put("charge_behavior", UserBehavior.chargeSource)
                put("sku", product.google)
            })

        } else {
            activity.lifecycleScope.launch {
                val list = payRepository.getPaymentList()
                dialog.dismissDialog()
                if (list.isNullOrEmpty()) {
                    EventBus.post(PayResultEvent.PayFailureEvent)
                    block?.invoke(false, "Payments is Null")
                    return@launch
                }
                if (list.size > 1) {
                    SettlementStoreActivity.startSettlementStoreActivity(
                        activity,
                        product,
                        fromPopCode,
                        list
                    )
                } else {
                    launchPay(activity, fromPopCode, product, list[0], block)
                    Analysis.track("payment_channel_click_pay", mutableMapOf<String, Any>().apply {
                        put("code", fromPopCode)
                        put("source", UserBehavior.root)
                        put("charge_behavior", UserBehavior.chargeSource)
                        put("sku", product.google)
                    })
                }
            }
        }
        Analysis.track("payment_channel_show", mutableMapOf<String, Any>().apply {
            put("code", fromPopCode)
            put("source", UserBehavior.root)
            put("charge_behavior", UserBehavior.chargeSource)
            put("sku", product.google)
        })

    }

    /**
     * 启动支付
     */
    fun launchPay(
        activity: FragmentActivity,
        popCode: String,
        product: Product,
        payment: Payment,
        block: ((result: Boolean, msg: String) -> Unit)?
    ) {
        this.payResult = block
        activity.lifecycleScope.launch {
            invokeRealPay(activity, popCode, product, payment)
        }
    }

    /**
     * 对谷歌订单进行查询补单的行为
     */
    fun fixGoogleOrder(context: Context) {
        if (context.userDataStore.readToken().isEmpty()) return
        PayClient.get().queryGooglePurchase(context) { purchaseToken, purchaseOrderId, originJson ->
            payScope.launch {
                if (!purchaseOrderId.isNullOrEmpty() && !originJson.isNullOrEmpty()) {
                    payRepository.queryGoogleOrder(purchaseOrderId, originJson)
                }
            }
        }
    }

    private suspend fun getPreOrder(
        activity: Activity,
        popCode: String,
        product: Product,
        payment: Payment
    ): Order? {
        dialog.showDialog(activity, null)
        val data = payRepository.getPreOrder(
            popCode,
            UserBehavior.root,
            UserBehavior.chargeSource,
            product.id,
            payment.id
        )
        return if (data.data != null) {
            data.data
        } else {
            EventBus.post(PayResultEvent.PayFailureEvent)
            payResult?.invoke(false, "Order is Null:${data.msg}")
            dialog.dismissDialog()
            null
        }
    }

    private suspend fun invokeRealPay(
        activity: Activity,
        popCode: String,
        product: Product,
        payment: Payment
    ) {
        val pay = Pay()
        val order = getPreOrder(activity, popCode, product, payment)
        if (order == null) {
            EventBus.post(PayResultEvent.PayFailureEvent)
            payResult?.invoke(false, "Order is Null")
            return
        }
        when (payment.type) {
            "native" -> {
                pay.generateGooglePay(
                    product.isSubscribe,
                    product.google,
                    "${activity.userDataStore.getUid()}",
                    order.orderNo
                )
                PayClient.get().launchPay(activity, pay, this, NewWebPayActivity::class.java)
            }

            "web" -> {
                if (order.payUrl.isNullOrEmpty()) {
                    EventBus.post(PayResultEvent.PayFailureEvent)
                    payResult?.invoke(false, "Pay Url Null")
                } else {
                    pay.generateWebPay(
                        "${product.id}",
                        order.payUrl!!,
                        UserBehavior.chargeSource,
                    )
                    PayClient.get().launchPay(activity, pay, this, NewWebPayActivity::class.java)
                }

            }

            "browser" -> {
                if (order.payUrl.isNullOrEmpty()) {
                    EventBus.post(PayResultEvent.PayFailureEvent)
                    payResult?.invoke(false, "Pay Url Null")
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(order.payUrl)
                        activity.startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        ex.printStackTrace()
                        payResult?.invoke(false, "${ex.message}")
                    }
                }
            }

        }
        dialog.dismissDialog()

    }


    override fun onPaySuccess(payMethod: Int, orderNo: String?, extra: String?) {
        if (payMethod == PAY_MODE_GOOGLE) {
            payScope.launch {
                val response = payRepository.queryGoogleOrder(orderNo!!, extra!!)
                if (!response.isSuccess) {
                    Toaster.showShort(ActivityStack.application, "${response.msg}")
                }
            }
        }
        payScope.launch {
            dialog.dismissDialog()
        }
    }

    override fun onPayFail(payMethod: Int, orderNo: String?, extra: String?) {
        dialog.dismissDialog()
        payResult?.invoke(false, "onPayFail:$extra")
        EventBus.post(PayResultEvent.PayFailureEvent)
        Toaster.showShort(ActivityStack.application, "Pay Fail")
    }

    override fun onPayCancel(payMethod: Int, orderNo: String?, extra: String?) {
        dialog.dismissDialog()
        payResult?.invoke(false, "onPayCancel:$extra")
        EventBus.post(PayResultEvent.PayCancelEvent)
        Toaster.showShort(ActivityStack.application, "Pay Cancel")
    }
}