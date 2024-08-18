package com.amigo.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.logic.http.response.product.Product
import com.amigo.picture.loadImage
import com.amigo.store.R
import com.amigo.store.databinding.ItemCoinStoreProductExtraBinding
import com.amigo.uibase.gone
import com.amigo.uibase.visible

class CoinStoreExtraAdapter(context: Context) :
    BaseRvAdapter<Product, ItemCoinStoreProductExtraBinding>(context) {


    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemCoinStoreProductExtraBinding> {

        return BaseRvHolder(ItemCoinStoreProductExtraBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemCoinStoreProductExtraBinding, item: Product) {
        binding.ivCover.loadImage(item.cover ?: "", com.amigo.uibase.R.drawable.img_placehoder)
        binding.tvDiscount.text = "${item.discount}"
        binding.tvName.text = item.name
        binding.tvPrice.text = "${item.unit}${item.displayPrice}"
        binding.tvBound.text = item.bonusDescribe
        if (item.discount.isNullOrEmpty()) {
            binding.tvDiscount.gone()
        } else {
            binding.tvDiscount.visible()
        }

        if (item.bonusDescribe.isNullOrEmpty()) {
            binding.tvBound.gone()
        } else {
            binding.tvBound.visible()
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