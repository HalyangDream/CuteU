package com.cute.uibase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.picture.loadImage
import com.cute.uibase.databinding.ItemPicturePreviewBinding

class PicturePreviewAdapter(context: Context) :
    BaseRvAdapter<Any, ItemPicturePreviewBinding>(context) {

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemPicturePreviewBinding> {

        return BaseRvHolder(ItemPicturePreviewBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemPicturePreviewBinding, item: Any) {
        binding.ivPhoto.loadImage(item)
    }
}