package com.cute.pay

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * author : mac
 * date   : 2022/4/18
 *
 */
object GooglePayClient {

    private var billingClient: BillingClient? = null
    private var billingSetupCode = -101

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Google购买后的支付回调
     */
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        // To be implemented in a later section.
        handlePurchaseResult(billingResult, purchases)
    }

    /**
     * 开始链接Google的服务器
     */
    private fun startConnection(block: (code: Int) -> Unit) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                billingSetupCode = billingResult.responseCode
                block(billingResult.responseCode)
            }

            override fun onBillingServiceDisconnected() {
                block(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
            }
        })
    }

    /**
     * billing支付服务是否可用
     *
     * @return
     */
    fun isBillingReady(): Boolean {
        return billingClient != null && billingClient!!.isReady && billingSetupCode == BillingClient.BillingResponseCode.OK
    }


    fun initialize(context: Context, block: (code: Int) -> Unit) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
                .enablePendingPurchases().build()
        }
        if (!isBillingReady()) {
            startConnection(block)
        } else {
            block(BillingClient.BillingResponseCode.OK)
        }
    }


    /**
     * 查询Google商品详细信息
     */
    fun querySkuDetail(
        isSubscription: Boolean,
        skuIds: MutableList<String?>,
        block: (code: Int, skuList: MutableList<ProductDetails>?) -> Unit
    ) {
        scope.launch {
            val productList = mutableListOf<Product>()
            val productType =
                if (isSubscription) BillingClient.ProductType.SUBS else BillingClient.ProductType.INAPP
            for (skuId in skuIds) {
                Log.i("GooglePayClient", "skuId:${skuId}")
                val product =
                    Product.newBuilder().setProductId(skuId!!).setProductType(productType).build()
                productList.add(product)
            }
            val params = QueryProductDetailsParams.newBuilder()
            params.setProductList(productList)
            val result = billingClient?.queryProductDetails(params.build()) ?: ProductDetailsResult(
                BillingResult.newBuilder().setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                    .setDebugMessage("SERVICE DISCONNECTED").build(), null
            )
            val productDetailsList = result.productDetailsList
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isNullOrEmpty()) {
                block(result.billingResult.responseCode, productDetailsList.toMutableList())
            } else {
                block(result.billingResult.responseCode, null)
            }
        }
    }

    /**
     * 查询购买Google商品
     */
    fun queryInAppPurchases(block: (purchaseToken: String?, purchaseOrderId: String?, originJson: String?) -> Unit) {
        val param = QueryPurchasesParams.newBuilder()
        param.setProductType(BillingClient.ProductType.INAPP)
        billingClient?.queryPurchasesAsync(param.build()) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchaseList = purchasesList.filter { purchase ->
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }.toMutableList()
                purchaseList.let {
                    for (purchase in it) {
                        block(
                            purchase.purchaseToken,
                            purchase.accountIdentifiers?.obfuscatedProfileId,
                            purchase.originalJson
                        )
                    }
                }
            }
        }
    }

    /**
     * 查询购买Google商品
     */
    fun querySubsPurchases(block: (purchaseToken: String?, purchaseOrderId: String?, originJson: String?) -> Unit) {
        val param = QueryPurchasesParams.newBuilder()
        param.setProductType(BillingClient.ProductType.SUBS)
        billingClient?.queryPurchasesAsync(param.build()) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchaseList = purchasesList.filter { purchase ->
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }.toMutableList()
                purchaseList.let {
                    for (purchase in it) {
                        block(
                            purchase.purchaseToken,
                            purchase.accountIdentifiers?.obfuscatedProfileId,
                            purchase.originalJson
                        )
                    }
                }
            }
        }
    }

    private fun handlePurchaseResult(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingResponseCode.USER_CANCELED) {
            PayClient.get().handleGoogleResult(PAY_CANCEL, "", "User Cancel")
            return
        }


        if (result.responseCode == BillingResponseCode.OK) {
            if (purchases.isNullOrEmpty()) {
                PayClient.get().handleGoogleResult(PAY_FAIL, "", "Purchases is Null")
                return
            }
            scope.launch {
                val purchase = purchases[0]
                consumePurchase(purchase)
                acknowledgePurchase(purchase)
                PayClient.get().handleGoogleResult(
                    PAY_SUCCESS,
                    purchase.accountIdentifiers?.obfuscatedProfileId,
                    purchase.originalJson
                )
            }
        } else {
            PayClient.get().handleGoogleResult(
                PAY_FAIL,
                "",
                "${result.responseCode},${result.debugMessage},${purchases?.get(0)?.products}"
            )
        }
    }

    /**
     * 消耗已经购买的商品
     */
    private suspend fun consumePurchase(
        purchase: Purchase
    ): ConsumeResult {
        val consumeParams = ConsumeParams.newBuilder()
        consumeParams.setPurchaseToken(purchase.purchaseToken)
        val result = billingClient?.consumePurchase(consumeParams.build())
        return result ?: ConsumeResult(
            BillingResult.newBuilder().setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
                .setDebugMessage("SERVICE DISCONNECTED").build(), purchase.purchaseToken
        )
    }

    /**
     * 确认已经订阅的商品
     */
    private suspend fun acknowledgePurchase(
        purchase: Purchase
    ): BillingResult {
        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        val billingResult = billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
        return billingResult ?: BillingResult.newBuilder()
            .setResponseCode(BillingResponseCode.SERVICE_DISCONNECTED)
            .setDebugMessage("SERVICE DISCONNECTED").build()
    }


    /**
     * 购买Google商品
     */
    fun launchBillingFlow(
        userId: String, orderId: String, activity: Activity, productDetails: ProductDetails
    ): Int {
        val productDetailsParams = ProductDetailsParams.newBuilder()
        productDetailsParams.setProductDetails(productDetails)
        val offerTokenDetail = productDetails.subscriptionOfferDetails?.get(0)
        if (offerTokenDetail != null) {
            productDetailsParams.setOfferToken(offerTokenDetail.offerToken)
        }
        val flowParams = BillingFlowParams.newBuilder()
        flowParams.setObfuscatedAccountId(userId)
        flowParams.setObfuscatedProfileId(orderId)
        flowParams.setIsOfferPersonalized(true)
        flowParams.setProductDetailsParamsList(mutableListOf(productDetailsParams.build()))
        val result = billingClient?.launchBillingFlow(activity, flowParams.build())
        return result?.responseCode ?: BillingClient.BillingResponseCode.BILLING_UNAVAILABLE
    }
}