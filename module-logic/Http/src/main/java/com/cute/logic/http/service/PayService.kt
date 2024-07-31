package com.cute.logic.http.service

import com.cute.http.ApiResponse
import com.cute.logic.http.response.pay.Order
import com.cute.logic.http.response.pay.PaymentResponse
import com.cute.logic.http.response.product.ProductResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PayService {


    /**
     * 获取支付方式
     */
    @POST("/v1/payment/list")
    suspend fun getPaymentList(@Body body: RequestBody): ApiResponse<PaymentResponse>


    /**
     * 获取预订单
     */
    @POST("/v1/payment/pre_order")
    suspend fun getPreOrder(@Body body: RequestBody): ApiResponse<Order>


    /**
     * 查询google 支付订单
     */
    @POST("/v1/payment/check_google")
    suspend fun checkGoogleOrder(@Body body: RequestBody): ApiResponse<Any>

}