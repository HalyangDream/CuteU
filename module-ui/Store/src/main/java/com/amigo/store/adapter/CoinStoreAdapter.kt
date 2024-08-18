package com.amigo.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.logic.http.response.product.Product
import com.amigo.picture.loadImage
import com.amigo.store.databinding.ItemCoinStoreProductBinding
import com.amigo.store.databinding.ItemVipStoreProductBinding
import com.amigo.tool.dpToPx
import com.amigo.uibase.gone
import com.amigo.uibase.invisible
import com.amigo.uibase.visible

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
        binding.ivCover.loadImage(item.cover?:"", com.amigo.uibase.R.drawable.img_placehoder)
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