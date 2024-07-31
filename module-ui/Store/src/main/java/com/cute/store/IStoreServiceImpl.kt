package com.cute.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.logic.http.response.product.Product
import com.cute.pay.GooglePayClient
import com.cute.store.dialog.StoreDialog20101
import com.cute.store.dialog.StoreDialog20102
import com.cute.store.dialog.StoreDialog20201
import com.cute.store.dialog.StoreDialog20300
import com.cute.uibase.ActivityStack
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.provider.IStoreService

@Route(path = RoutePage.Provider.PRODUCT_PROVIDER)
class IStoreServiceImpl : IStoreService {

    override fun hasStoreCode(popCode: String): Boolean {
        return popCode == "20100" || popCode == "20101" || popCode == "20102"
                || popCode == "20200" || popCode == "20300" || popCode == "20201"
    }

    override fun visitWebPayActivity(context: Context, payUrl: String, source: String) {
        val intent = Intent(context, NewWebPayActivity::class.java)
        intent.putExtra("pay_url", payUrl)
        intent.putExtra("source", source)
        context.startActivity(intent)
    }

    override fun showCodeDialog(popCode: String, dialogBundle: Bundle?): Boolean {
        val activity = ActivityStack.getTopActivity()
        if (activity != null) {
            return showPopCodeDialog(activity, popCode, dialogBundle)
        }
        return false
    }


    override fun fixGoogleOrder(context: Context) {
        if (GooglePayClient.isBillingReady()) {
            PayViewModel.fixGoogleOrder(context)
        }
    }

    override fun init(context: Context?) {

    }


    private fun showPopCodeDialog(
        context: Context,
        popCode: String,
        dialogBundle: Bundle?
    ): Boolean {
        return when (popCode) {
            "20100" -> {
                val intent = Intent(context, CoinStoreActivity::class.java)
                intent.putExtra("isFromCode", true)
                context.startActivity(intent)
                true
            }

            "20101" -> {
                val dialog = StoreDialog20101()
                dialog.showDialog(context, dialogBundle)
                true
            }

            "20102" -> {
                val dialog = StoreDialog20102()
                dialog.showDialog(context, dialogBundle)
                true
            }

            "20200" -> {
                val intent = Intent(context, VipStoreActivity::class.java)
                intent.putExtra("isFromCode", true)
                context.startActivity(intent)
                true
            }

            "20201" -> {
                val dialog = StoreDialog20201()
                dialog.showDialog(context, dialogBundle)
                true
            }

            "20300" -> {
                val dialog = StoreDialog20300()
                dialog.showDialog(context, dialogBundle)
                true
            }

            else -> false
        }
    }


}