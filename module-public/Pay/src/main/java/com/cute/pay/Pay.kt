package com.cute.pay

import android.content.Context
import com.cute.pay.annotation.PayMode

/**
 * author : mac
 * date   : 2022/4/20
 *
 */
const val PAY_MODE_GOOGLE = 1
const val PAY_MODE_WEB = 2

class Pay {

    @PayMode
    var pay: Int = PAY_MODE_GOOGLE

    /**
     * 商品ID
     */
    var skuId: String = ""

    var userId: String = ""

    /**
     * 预订单号
     */
    var orderNo: String = ""

    /**
     * web支付的链接
     */
    var webPayUrl: String? = ""

    /**
     * 一些跳转三方的intent地址
     */
    var intentUrl: String? = ""

    /**
     * 支付来源
     */
    var source: String? = ""
    var isSub = false

    /**
     * 生成Google支付的方式
     */
    fun generateGooglePay(isSub: Boolean, skuId: String, userId: String, orderNo: String) {
        this.isSub = isSub
        this.skuId = skuId
        this.userId = userId
        this.orderNo = orderNo
        this.pay = PAY_MODE_GOOGLE
    }


    /**
     * 生成Web支付的方式
     */
    fun generateWebPay(skuId: String, webPayUrl: String, source: String?) {
        this.skuId = skuId
        this.webPayUrl = webPayUrl
        this.pay = PAY_MODE_WEB
        this.source = source
    }
}
