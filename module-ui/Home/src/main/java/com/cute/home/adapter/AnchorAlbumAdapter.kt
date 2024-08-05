package com.cute.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.home.R
import com.cute.home.databinding.ItemAnchorAlbumBinding
import com.cute.logic.http.response.user.UserAlbum
import com.cute.picture.loadImage
import com.cute.picture.loadVideo
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.dpToPx
import com.cute.uibase.adapter.BaseRvFooterAdapter
import com.cute.uibase.gone
import com.cute.uibase.invisible
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible

class AnchorAlbumAdapter(context: Context) :
    BaseRvFooterAdapter<UserAlbum>(context) {

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {

        return AnchorAlbumHolder(mLayoutInflater.inflate(R.layout.item_anchor_album, parent, false))
    }

    override fun bindMainData(
        position: Int,
        item: UserAlbum?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemAnchorAlbumBinding
        item?.let { it ->
            if (it.isVideo) {
                if (!it.videoCover.isNullOrEmpty()) {
                    itemBind.ivAlbum.loadImage(
                        it.videoCover!!,
                        roundedCorners = 12f,
                        blurTransformation = if (it.isLock) BlurTransformation(
                            context,
                            25f,
                            3f
                        ) else null
                    )
                } else {
                    itemBind.ivAlbum.loadVideo(
                        it.resUrl,
                        roundedCorners = 12f,
                        blurTransformation = if (it.isLock) BlurTransformation(
                            context,
                            25f,
                            3f
                        ) else null
                    )
                }

            } else {
                itemBind.ivAlbum.loadImage(
                    it.resUrl,
                    roundedCorners = 12f,
                    placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
                    errorRes = com.cute.uibase.R.drawable.img_placehoder,
                    blurTransformation = if (it.isLock) BlurTransformation(
                        context,
                        25f,
                        3f
                    ) else null
                )
            }

            if (it.isLock) {
                itemBind.ivVideo.invisible()
                itemBind.ivLock.visible()
                itemBind.tvPravite.visible()
            } else {
                itemBind.ivLock.invisible()
                itemBind.tvPravite.invisible()
                if (it.isVideo) itemBind.ivVideo.visible() else itemBind.ivVideo.invisible()
            }
        }


    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        val item = getItem(position) ?: return
        if (item.isLock) {
            RouteSdk.navigationVipStore()
            UserBehavior.setChargeSource("unlock_album")
        } else {
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

    override fun getItemCount(items: List<UserAlbum>): Int {
        return  super.getItemCount(items)
    }

    class AnchorAlbumHolder(val view: View) : MultiHolder<ItemAnchorAlbumBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemAnchorAlbumBinding =
            ItemAnchorAlbumBinding.bind(itemView)
    }
}