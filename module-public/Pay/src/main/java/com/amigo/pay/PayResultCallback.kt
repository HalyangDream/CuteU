package com.amigo.pay

import com.amigo.pay.annotation.PayMode

/**
 * author : mac
 * date   : 2022/4/28
 * 
 */
interface PayResultCallback {

    /**
     * @param payMethod 支付的方式
     * @param orderNo，订单号，不一定有
     * @param extra 额外携带的信息
     */
    fun onPaySuccess(@PayMode payMethod: Int, orderNo: String?, extra: String?)

    /**
     * @param payMethod 支付的方式
     * @param orderNo，订单号，不一定有
     * @param extra 额外携带的信息
     */
    fun onPayFail(@PayMode payMethod: Int, orderNo: String?, extra: String?)

    /**
     * @param payMethod 支付的方式
     * @param orderNo，订单号，不一定有
     * @param extra 额外携带的信息
     */
    fun onPayCancel(@PayMode payMethod: Int, orderNo: String?, extra: String?)

}