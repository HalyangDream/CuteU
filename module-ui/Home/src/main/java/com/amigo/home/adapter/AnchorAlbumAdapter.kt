package com.amigo.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.amigo.home.R
import com.amigo.home.databinding.ItemAnchorAlbumBinding
import com.amigo.logic.http.response.user.UserAlbum
import com.amigo.picture.loadImage
import com.amigo.picture.loadVideo
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.tool.dpToPx
import com.amigo.uibase.adapter.BaseRvFooterAdapter
import com.amigo.uibase.gone
import com.amigo.uibase.invisible
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.media.preview.VideoPreviewActivity
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible

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
                    placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
                    errorRes = com.amigo.uibase.R.drawable.img_placehoder,
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