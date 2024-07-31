package com.cute.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.chat.databinding.ItemChatHeaderAlbumAdapterBinding
import com.cute.logic.http.response.user.ChatUserAlbum
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.tool.dpToPx
import com.cute.uibase.invisible
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.visible

class ChatHeaderAlbumAdapter(context: Context) :
    BaseRvAdapter<ChatUserAlbum, ItemChatHeaderAlbumAdapterBinding>(context) {

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemChatHeaderAlbumAdapterBinding> {

        return BaseRvHolder(
            ItemChatHeaderAlbumAdapterBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun bindData(
        position: Int,
        binding: ItemChatHeaderAlbumAdapterBinding,
        item: ChatUserAlbum
    ) {
        if (item.isVideo) {
            if (!item.videoCover.isNullOrEmpty()) {
                binding.ivAlbum.loadImage(
                    item.videoCover!!,
                    roundedCorners = 12f.dpToPx(mContext),
                    placeholderRes = com.cute.uibase.R.drawable.img_placehoder_grey,
                    errorRes = com.cute.uibase.R.drawable.img_placehoder_grey
                )
            } else {
                binding.ivAlbum.loadVideo(
                    item.resUrl,
                    roundedCorners = 12f.dpToPx(mContext),
                    placeholderRes = com.cute.uibase.R.drawable.img_placehoder_grey,
                    errorRes = com.cute.uibase.R.drawable.img_placehoder_grey
                )
            }
            binding.ivVideo.visible()
        } else {
            binding.ivAlbum.loadImage(
                item.resUrl,
                roundedCorners = 12f.dpToPx(mContext),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder_grey,
                errorRes = com.cute.uibase.R.drawable.img_placehoder_grey
            )
            binding.ivVideo.invisible()
        }

        binding.root.setOnClickListener {
            if (item.isVideo) {
                VideoPreviewActivity.startPreview(context, item.resUrl)
            } else {
                val list =
                    items.filter { !it.isVideo && !it.isLock }.map { it.resUrl }.toTypedArray()
                val start = list.indexOf(item.resUrl)
                PicturePreviewActivity.startPreview(context, list, start)
            }
        }
    }
}