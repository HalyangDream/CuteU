package com.amigo.store.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.amigo.analysis.Analysis
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.logic.http.model.ProductRepository
import com.amigo.logic.http.response.product.Product
import com.amigo.store.PayViewModel
import com.amigo.store.databinding.DialogStore20300Binding
import com.amigo.tool.EventBus
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.event.StoreDialogCloseEvent
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior
import kotlinx.coroutines.launch

class StoreDialog20301 : BaseCenterDialog() {

    private val productRepository = ProductRepository()
    private lateinit var binding: DialogStore20300Binding
    private var product: Product? = null

    override fun setDialogHeightRate(): Float {
        return -1f
    }

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogStore20300Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        binding.btnBuy.setThrottleListener {
            if (product != null) {
                startPay(product!!)
            }
        }

        binding.ivClose.setOnClickListener {
            AdPlayService.reportPlayAdScenes("close_code", "20301")
            dismissDialog()
        }
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20301")
            put("source", UserBehavior.chargeSource)
        })
        Analysis.track("payment_popup_show",mutableMapOf<String, Any>().apply {
            put("code", "20301")
            put("source", UserBehavior.root)
            put("charge_behavior",UserBehavior.chargeSource)
        })
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = productRepository.getCoinPackage20301()
            if (response.data != null) {
                product = response.data
                binding.tvName.text = "${product?.name}"
                binding.tvBound.text = "${product?.bonusDescribe}"
                binding.tvPrice.text = "${product?.unit}${product?.displayPrice}"
            }
        }
    }

    override fun dismissDialog() {
        super.dismissDialog()
        EventBus.post(StoreDialogCloseEvent("20301"))
        ReportBehavior.reportCloseCoinLessWindow("20301")
    }

    private fun startPay(product: Product) {
        if (activity == null) return
        PayViewModel.launchSettlementStore(requireActivity(), product, "20301") { result, msg ->
            if (result) {
                Toaster.showShort(ActivityStack.application, "Pay Success")
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20301")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
        Analysis.track("payment_popup_click_sku",mutableMapOf<String, Any>().apply {
            put("code", "20301")
            put("source", UserBehavior.root)
            put("charge_behavior",UserBehavior.chargeSource)
            put("sku",product.google)
        })
    }
}