package com.cute.store.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cute.analysis.Analysis
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.response.product.Product
import com.cute.store.PayViewModel
import com.cute.store.databinding.DialogStore20300Binding
import com.cute.uibase.ReportBehavior
import com.cute.tool.EventBus
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.StoreDialogCloseEvent
import com.cute.uibase.userbehavior.UserBehavior
import kotlinx.coroutines.launch

class StoreDialog20300 : BaseCenterDialog() {

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
        binding.tvBuy.setOnClickListener {
            if (product != null) {
                startPay(product!!)
            }
        }

        binding.ivClose.setOnClickListener {
            AdPlayService.reportPlayAdScenes("close_code", "20300")
            dismissDialog()
        }
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20300")
            put("source", UserBehavior.chargeSource)
        })
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = productRepository.getCoinPackage20300()
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
        EventBus.post(StoreDialogCloseEvent("20300"))
        ReportBehavior.reportCloseCoinLessWindow("20300")
    }

    private fun startPay(product: Product) {
        if (activity == null) return
        PayViewModel.launchSettlementStore(requireActivity(), product, "20300") { result, msg ->
            if (result) {
                Toaster.showShort(ActivityStack.application, "Pay Success")
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20300")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }
}