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
import com.amigo.uibase.ReportBehavior
import com.amigo.store.databinding.DialogStore20201Binding
import com.amigo.tool.EventBus
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.event.StoreDialogCloseEvent
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.userbehavior.UserBehavior
import kotlinx.coroutines.launch

class StoreDialog20201 : BaseCenterDialog() {

    private val productRepository = ProductRepository()
    private lateinit var binding: DialogStore20201Binding
    private var product: Product? = null
    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogStore20201Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        binding.ivClose.setOnClickListener {
            AdPlayService.reportPlayAdScenes("close_code", "20201")
            dismissDialog()
        }

        binding.btnPrice.setThrottleListener {
            if (product != null) {
                startPay(product!!)
            }
        }

        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20201")
            put("source", UserBehavior.chargeSource)
        })
        Analysis.track("pop_recharge_20201")
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = productRepository.getVipProduct20201()
            if (response.data != null) {
                product = response.data
//                binding.tvProductName.text = "${product?.name}"
                binding.stvDesc.text = "${product?.bonusDescribe}"
                binding.btnPrice.text = "${product?.unit}${product?.displayPrice}"
            }
        }
    }

    override fun dismissDialog() {
        super.dismissDialog()
        EventBus.post(StoreDialogCloseEvent("20201"))
        ReportBehavior.reportCloseCoinLessWindow("20201")
    }

    private fun startPay(product: Product) {
        if (activity == null) return
        PayViewModel.launchSettlementStore(requireActivity(), product, "20201") { result, msg ->
            if (result) {
                Toaster.showShort(ActivityStack.application, "Pay Success")
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, msg)
            }

        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20201")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }
}