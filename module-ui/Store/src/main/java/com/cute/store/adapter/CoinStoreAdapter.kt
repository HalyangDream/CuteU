package com.cute.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.logic.http.response.product.Product
import com.cute.picture.loadImage
import com.cute.store.databinding.ItemCoinStoreProductBinding
import com.cute.store.databinding.ItemVipStoreProductBinding
import com.cute.tool.dpToPx
import com.cute.uibase.gone
import com.cute.uibase.invisible
import com.cute.uibase.visible

class CoinStoreAdapter(context: Context) :
    BaseRvAdapter<Product, ItemCoinStoreProductBinding>(context) {


    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemCoinStoreProductBinding> {

        return BaseRvHolder(ItemCoinStoreProductBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemCoinStoreProductBinding, item: Product) {
        binding.ivCover.loadImage(item.cover?:"", com.cute.uibase.R.drawable.img_placehoder)
        binding.tvDiscount.text = "${item.discount}"
        binding.tvName.text = item.name
        binding.tvPrice.text = "${item.unit}${item.displayPrice}"
        binding.tvBoundDesc.text = item.bonusDescribe
        if (item.discount.isNullOrEmpty()) {
            binding.tvDiscount.gone()
        } else {
            binding.tvDiscount.visible()
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