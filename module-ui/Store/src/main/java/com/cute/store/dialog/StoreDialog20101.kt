package com.cute.store.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.cute.analysis.Analysis
import com.cute.basic.dialog.BaseBottomDialog
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.response.product.Product
import com.cute.picture.loadImage
import com.cute.store.PayViewModel
import com.cute.uibase.ReportBehavior
import com.cute.store.databinding.DialogStore20101Binding
import com.cute.store.databinding.DialogStore20101ItemBinding
import com.cute.tool.EventBus
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.StoreDialogCloseEvent
import com.cute.uibase.gone
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.launch

class StoreDialog20101 : BaseBottomDialog() {

    private val productRepository = ProductRepository()
    private lateinit var binding: DialogStore20101Binding
    private lateinit var coinAdapter: DialogCoinAdapter20101
    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogStore20101Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        binding.ivClose.setOnClickListener {
            AdPlayService.reportPlayAdScenes("close_code", "20101")
            dismissDialog()
        }

        binding.rvProduct.apply {
            coinAdapter = DialogCoinAdapter20101(context)
            adapter = coinAdapter
            coinAdapter.setOnClickItemProductListener {
                startPay(it)
            }
        }
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type", "20101")
            put("source", UserBehavior.chargeSource)
        })
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = productRepository.getCoinProduct20101()
            val extraProducts = response.data?.extraProduct
            val list = response.data?.list
            if (extraProducts != null) {
                list?.addAll(0, extraProducts)
            }
            coinAdapter.setExtraProduct(extraProducts)
            coinAdapter.submitList(list)
        }
    }

    override fun dismissDialog() {
        super.dismissDialog()
        EventBus.post(StoreDialogCloseEvent("20101"))
        ReportBehavior.reportCloseCoinLessWindow("20101")
    }

    private fun startPay(product: Product) {
        if (activity == null) return
        PayViewModel.launchSettlementStore(requireActivity(), product, "20101") { result, msg ->
            if (result) {
                Toaster.showShort(ActivityStack.application, "Pay Success")
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type", "20101")
            put("source", UserBehavior.chargeSource)
            put("item_name", product.name)
            put("item_money_amount", product.displayPrice)
        })
    }

    private inner class DialogCoinAdapter20101(context: Context) :
        BaseRvAdapter<Product, DialogStore20101ItemBinding>(context) {

        private var extraProducts: MutableList<Product>? = null
        fun setExtraProduct(extraProducts: MutableList<Product>?) {
            this.extraProducts = extraProducts
        }

        override fun bindViewBinding(
            context: Context, parent: ViewGroup, viewType: Int, layoutInflater: LayoutInflater
        ): BaseRvHolder<DialogStore20101ItemBinding> {

            return BaseRvHolder(DialogStore20101ItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun bindData(position: Int, binding: DialogStore20101ItemBinding, item: Product) {
            binding.ivProduct.loadImage("${item.cover}")
            binding.tvName.text = item.name
            binding.tvBound.text = "${item.bonusDescribe}"
            binding.tvPrice.text = "${item.unit}${item.displayPrice}"
            binding.tvDiscount.text = "${item.discount}"
            if (item.discount.isNullOrEmpty()) {
                binding.tvDiscount.gone()
            } else {
                binding.tvDiscount.visible()
            }
            if (!extraProducts.isNullOrEmpty() && extraProducts!!.contains(item)) {
                binding.srlContent.shapeDrawableBuilder.setSolidColor(Color.parseColor("#FFF6E6"))
                    .intoBackground()
            } else {
                binding.srlContent.shapeDrawableBuilder.setSolidColor(Color.parseColor("#F3F3F3"))
                    .intoBackground()
            }

            binding.root.setOnClickListener {
                block?.invoke(item)
            }
        }

        private var block: ((Product) -> Unit)? = null
        fun setOnClickItemProductListener(block: ((Product) -> Unit)?) {
            this.block = block
        }
    }
}