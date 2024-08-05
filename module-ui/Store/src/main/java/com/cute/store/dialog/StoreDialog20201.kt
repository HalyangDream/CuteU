package com.cute.store.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.response.product.Product
import com.cute.store.PayViewModel
import com.cute.uibase.ReportBehavior
import com.cute.store.databinding.DialogStore20201Binding
import com.cute.tool.EventBus
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.StoreDialogCloseEvent
import com.cute.uibase.setThrottleListener
import com.cute.uibase.userbehavior.UserBehavior
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