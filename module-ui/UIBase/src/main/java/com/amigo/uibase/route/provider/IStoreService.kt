package com.amigo.uibase.route.provider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.template.IProvider
import com.amigo.basic.dialog.BaseDialog
import com.amigo.logic.http.response.call.DeviceFunctionEnum
import com.amigo.logic.http.response.call.DeviceFunctionInfo
import com.amigo.logic.http.response.product.Product

interface IStoreService : IProvider {


    fun hasStoreCode(popCode: String): Boolean

    /**
     * @return 是否被处理了
     */
    fun showCodeDialog(popCode: String, dialogBundle: Bundle?): Boolean

    fun showPopCodeDialog(popCode: String, dialogBundle: Bundle?): BaseDialog?


    /**
     * 修复Google订单
     * 补单机制
     */
    fun fixGoogleOrder(context: Context)

    /**
     * 通过活动访问H5支付
     */
    fun visitWebPayActivity(context: Context, payUrl: String, source: String)

}