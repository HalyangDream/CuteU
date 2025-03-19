package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.pay.Order
import com.amigo.logic.http.response.pay.Payment
import com.amigo.logic.http.response.product.Product
import com.amigo.logic.http.service.PayService

class PayRepository : ApiRepository() {


    private val service = ApiClient.getService(PayService::class.java)


    suspend fun getPaymentList(): ArrayList<Payment>? {
        val param = HttpCommonParam.getCommonParam()
        val response = launchRequest { service.getPaymentList(param.toRequestBody()) }
        return response.data?.payment
    }

    suspend fun getPreOrder(
        popCode:String,
        source: String,
        root: String,
        productId: Int,
        paymentId: Int
    ): ApiResponse<Order> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("product_id", productId)
            put("payment_id", paymentId)
            put("pop_code", popCode)
            put("source", source)
            put("root", root)
        }
        return launchRequest { service.getPreOrder(param.toRequestBody()) }
    }

    suspend fun queryGoogleOrder(orderNo: String, purchaseToken: String): ApiResponse<Any> {
        val param = HttpCommonParam.getCommonParam().apply {
            put("purchase_token", purchaseToken)
            put("order_no", orderNo)
        }
        return launchRequest { service.checkGoogleOrder(param.toRequestBody()) }
    }
}