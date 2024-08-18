package com.amigo.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.chat.databinding.ItemChatHeaderAlbumAdapterBinding
import com.amigo.logic.http.response.user.ChatUserAlbum
import com.amigo.picture.loadImage
import com.amigo.picture.loadVideo
import com.amigo.tool.dpToPx
import com.amigo.uibase.invisible
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.media.preview.VideoPreviewActivity
import com.amigo.uibase.visible

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
                    placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_grey,
                    errorRes = com.amigo.uibase.R.drawable.img_placehoder_grey
                )
            } else {
                binding.ivAlbum.loadVideo(
                    item.resUrl,
                    roundedCorners = 12f.dpToPx(mContext),
                    placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_grey,
                    errorRes = com.amigo.uibase.R.drawable.img_placehoder_grey
                )
            }
            binding.ivVideo.visible()
        } else {
            binding.ivAlbum.loadImage(
                item.resUrl,
                roundedCorners = 12f.dpToPx(mContext),
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder_grey,
                errorRes = com.amigo.uibase.R.drawable.img_placehoder_grey
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