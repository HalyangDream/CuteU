package com.amigo.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.logic.http.response.product.Product
import com.amigo.pay.GooglePayClient
import com.amigo.store.R
import com.amigo.store.databinding.ItemVipStoreProductBinding
import com.amigo.tool.dpToPx
import com.amigo.uibase.invisible
import com.amigo.uibase.visible

class VipStoreAdapter(context: Context) :
    BaseRvAdapter<Product, ItemVipStoreProductBinding>(context) {

    private var select = 1
    private val itemWidth = (context.resources.displayMetrics.widthPixels - 40.dpToPx(context)) / 3


    fun getSelectProduct(): Product? {
        return getItem(select)
    }

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemVipStoreProductBinding> {

        return BaseRvHolder(ItemVipStoreProductBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemVipStoreProductBinding, item: Product) {
        binding.srlRoot.layoutParams.width = itemWidth
        binding.stvDiscount.text = "${item.discount}"
        binding.tvName.text = "${item.name}${item.subName} VIP"
        getGooglePlayPrice(item) { price ->
            if (price.isNullOrEmpty()) {
                binding.tvPrice.text = "${item.unit}${item.displayPrice}/${timeUnit(item)}"
            } else {
                binding.tvPrice.text = price
            }
        }

        binding.tvBoundDesc.text = context.getString(
            com.amigo.uibase.R.string.str_billed_vip_value,
            timeUnit(item), "$${item.googlePrice}"
        )
        if (item.discount.isNullOrEmpty()) {
            binding.stvDiscount.invisible()
        } else {
            binding.stvDiscount.visible()
        }

        if (select == position) {
            selectUiStyle(binding)
        } else {
            defaultUiStyle(binding)
        }
        binding.srlRoot.setOnClickListener {
            select = position
            notifyItemRangeChanged(0, itemCount)
        }
    }


    private fun selectUiStyle(binding: ItemVipStoreProductBinding) {
        binding.srlRoot.shapeDrawableBuilder
            .setStrokeColor(Color.parseColor("#32E1F0"))
            .setStrokeWidth(2.dpToPx(context)).intoBackground()
        binding.stvDiscount.shapeDrawableBuilder
            .setSolidColor(Color.parseColor("#32E1F0"))
            .intoBackground()
        binding.stvDiscount.setTextColor(Color.parseColor("#000000"))
        binding.tvBoundDesc.setTextColor(Color.parseColor("#32E1F0"))
        binding.tvPrice.setTextColor(Color.parseColor("#CCFFFFFF"))
        binding.tvName.setTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun defaultUiStyle(binding: ItemVipStoreProductBinding) {
        binding.srlRoot.shapeDrawableBuilder
            .setStrokeColor(Color.parseColor("#00000000"))
            .intoBackground()
        binding.stvDiscount.shapeDrawableBuilder
            .setSolidColor(Color.parseColor("#000000"))
            .intoBackground()
        binding.stvDiscount.setTextColor(Color.parseColor("#99FFFFFF"))
        binding.tvBoundDesc.setTextColor(Color.parseColor("#99FFFFFF"))
        binding.tvPrice.setTextColor(Color.parseColor("#99FFFFFF"))
        binding.tvName.setTextColor(Color.parseColor("#99FFFFFF"))
    }

    private fun getGooglePlayPrice(item: Product, block: (String?) -> Unit) {
        GooglePayClient.getSkuPrice(
            item.isSubscribe,
            item.google
        ) {
            block(it)
        }
    }

    private fun timeUnit(item: Product): String {
        if (item.subName?.lowercase() == "month") {
            return "Month"
        }
        if (item.subName?.lowercase() == "year") {
            return "Year"
        }

        if (item.subName?.lowercase() == "days") {
            return "Week"
        }
        return ""
    }

    /**
     * 计算每天或者每年的价格
     */
    private fun calcMinUnitPrice(item: Product): String? {
        try {
            if (item.subName?.lowercase() == "month") {
                return null
            }
            if (item.subName?.lowercase() == "year") {
                val usdPrice = item.googlePrice as Double
                return "${usdPrice / 12}"
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}