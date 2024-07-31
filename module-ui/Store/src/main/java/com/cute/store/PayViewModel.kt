package com.cute.store

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.cute.baselogic.userDataStore
import com.cute.logic.http.model.PayRepository
import com.cute.logic.http.response.pay.Order
import com.cute.logic.http.response.pay.Payment
import com.cute.logic.http.response.product.Product
import com.cute.pay.PAY_MODE_GOOGLE
import com.cute.pay.Pay
import com.cute.pay.PayClient
import com.cute.pay.PayResultCallback
import com.cute.tool.EventBus
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.Constant
import com.cute.uibase.DefaultLoadingDialog
import com.cute.uibase.event.PayResultEvent
import com.cute.uibase.userbehavior.UserBehavior
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
        if (activity == null) return
        this.payResult = block
        dialog.showDialog(activity, null)
        if (activity.userDataStore.role() == "4") {
            launchPay(activity, product, googlePay, block)
            dialog.dismissDialog()
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
                    launchPay(activity, product, list[0], block)
                }
            }
        }

    }

    /**
     * 启动支付
     */
    fun launchPay(
        activity: FragmentActivity,
        product: Product,
        payment: Payment,
        block: ((result: Boolean, msg: String) -> Unit)?
    ) {
        this.payResult = block
        activity.lifecycleScope.launch {
            invokeRealPay(activity, product, payment)
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
        product: Product,
        payment: Payment
    ): Order? {
        dialog.showDialog(activity, null)
        val data = payRepository.getPreOrder(
            UserBehavior.chargeSource,
            UserBehavior.root,
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
        product: Product,
        payment: Payment
    ) {
        val pay = Pay()
        val order = getPreOrder(activity, product, payment)
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
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(order.payUrl)
                    activity.startActivity(intent)
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