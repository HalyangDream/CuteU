package com.amigo.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.logic.http.response.product.Product
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
        binding.tvName.text = item.name
        binding.tvSubName.text = "${item.subName}"
        binding.tvPrice.text = "${item.unit+item.displayPrice}"
        binding.tvBoundDesc.text = "${item.describe}"
        if (item.discount.isNullOrEmpty()) {
            binding.stvDiscount.invisible()
        } else {
            binding.stvDiscount.visible()
        }

        if (item.describe.isNullOrEmpty()) {
            binding.tvBoundDesc.invisible()
        } else {
            binding.tvBoundDesc.visible()
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
        binding.tvSubName.setTextColor(Color.parseColor("#FFFFFF"))
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
        binding.tvSubName.setTextColor(Color.parseColor("#99FFFFFF"))
    }
}