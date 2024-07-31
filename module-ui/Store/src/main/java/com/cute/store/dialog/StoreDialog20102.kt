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
import com.cute.logic.http.model.ProfileRepository
import com.cute.logic.http.model.UserRepository
import com.cute.logic.http.response.product.Product
import com.cute.picture.loadImage
import com.cute.store.PayViewModel
import com.cute.store.R
import com.cute.store.databinding.DialogStore20102Binding
import com.cute.store.databinding.DialogStore20102ItemBinding
import com.cute.uibase.ReportBehavior
import com.cute.tool.EventBus
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.event.StoreDialogCloseEvent
import com.cute.uibase.gone
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreDialog20102 : BaseBottomDialog() {

    private val productRepository = ProductRepository()
    private val userRepository = UserRepository()
    private val profileRepository = ProfileRepository()

    private lateinit var binding: DialogStore20102Binding
    private lateinit var coinAdapter: DialogCoinAdapter20102

    private var anchorId: Long? = null

    override fun parseBundle(bundle: Bundle?) {
        anchorId = bundle?.getLong("anchorId")
    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogStore20102Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        binding.ivClose.setOnClickListener {
            AdPlayService.reportPlayAdScenes("close_code","20102")
            dismissDialog()
        }

        binding.rvProduct.apply {
            coinAdapter = DialogCoinAdapter20102(context)
            adapter = coinAdapter
            coinAdapter.setOnClickItemProductListener {
                startPay(it)
            }
        }
        ReportBehavior.reportEvent("pop_recharge", mutableMapOf<String, Any>().apply {
            put("pop_type","20102")
            put("source",UserBehavior.chargeSource)
        })
    }

    override fun initData() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                if (anchorId != null) {
                    val response = userRepository.getUserDetail(anchorId!!)
                    val detail = response.data
                    if (detail != null) {
                        binding.ivAnchorAvatar.loadImage(
                            detail.avatar,
                            isCircle = true,
                            placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round_grey
                        )
                        binding.tvContent.text = context?.getString(
                            com.cute.uibase.R.string.str_dialog_20102_tip,
                            detail.callPrice
                        )
                    }
                }
            }

            withContext(Dispatchers.Main) {
                val response = profileRepository.getProfileInfo()
                val profile = response.data
                if (profile != null) {
                    binding.ivUserAvatar.loadImage(
                        profile.avatar, isCircle = true,
                        placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round_grey
                    )
                }
            }

            withContext(Dispatchers.Main) {
                val response = productRepository.getCoinProduct20102()
                val extraProducts = response.data?.extraProduct
                val list = response.data?.list
                if (extraProducts != null) {
                    list?.addAll(0, extraProducts)
                }
                coinAdapter.setExtraProduct(extraProducts)
                coinAdapter.submitList(list)
            }

        }
    }

    override fun dismissDialog() {
        super.dismissDialog()
        EventBus.post(StoreDialogCloseEvent("20102"))
        ReportBehavior.reportCloseCoinLessWindow("20102")
    }

    private fun startPay(product: Product) {
        if (activity == null) return
        PayViewModel.launchSettlementStore(requireActivity(), product,"20102") { result, msg ->
            if (result) {
                Toaster.showShort(ActivityStack.application, "Pay Success")
                dismissDialog()
            } else {
                Toaster.showShort(ActivityStack.application, msg)
            }
        }
        ReportBehavior.reportEvent("pop_recharge_continue", mutableMapOf<String, Any>().apply {
            put("pop_type","20102")
            put("source",UserBehavior.chargeSource)
            put("item_name",product.name)
            put("item_money_amount",product.displayPrice)
        })
    }

    private inner class DialogCoinAdapter20102(context: Context) :
        BaseRvAdapter<Product, DialogStore20102ItemBinding>(context) {

        private var extraProducts: MutableList<Product>? = null
        fun setExtraProduct(extraProducts: MutableList<Product>?) {
            this.extraProducts = extraProducts
        }

        override fun bindViewBinding(
            context: Context, parent: ViewGroup, viewType: Int, layoutInflater: LayoutInflater
        ): BaseRvHolder<DialogStore20102ItemBinding> {

            return BaseRvHolder(DialogStore20102ItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun bindData(position: Int, binding: DialogStore20102ItemBinding, item: Product) {
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