package com.amigo.uibase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.picture.loadImage
import com.amigo.uibase.databinding.ItemPicturePreviewBinding

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