package com.amigo.mine.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amigo.basic.recycler.MultiLayoutRvAdapter
import com.amigo.logic.http.response.profile.ProfileAlbum
import com.amigo.mine.R
import com.amigo.mine.databinding.ItemProfileAlbumAddBinding
import com.amigo.mine.databinding.ItemProfileAlbumBinding
import com.amigo.picture.loadImage
import com.amigo.picture.loadVideo
import com.amigo.tool.dpToPx
import com.amigo.uibase.adapter.BaseRvFooterAdapter
import com.amigo.uibase.gone
import com.amigo.uibase.media.preview.PicturePreviewActivity
import com.amigo.uibase.visible

class ProfileAlbumAdapter(context: Context) : MultiLayoutRvAdapter<ProfileAlbum>(context) {

    private companion object {
        const val ADD_ALBUM = 0
        const val ALBUM = 1
    }

    private var openAlbum: () -> Unit = {}
    private var deleteAlbum: (ProfileAlbum) -> Unit = {}
    private var showAlbum: (ProfileAlbum) -> Unit = {}
    private var playVideo: (ProfileAlbum) -> Unit = {}

    override fun itemViewTypes(): IntArray {
        return intArrayOf(ADD_ALBUM, ALBUM)
    }

    override fun fullSpanItem(vieType: Int): Boolean = false

    override fun createHolder(vieType: Int, parent: ViewGroup): MultiHolder<out ViewBinding> {
        if (vieType == ADD_ALBUM) {
            return AddProfileAlbumHolder(
                mLayoutInflater.inflate(
                    R.layout.item_profile_album_add,
                    parent,
                    false
                )
            )
        } else {
            return ProfileAlbumHolder(
                mLayoutInflater.inflate(
                    R.layout.item_profile_album,
                    parent,
                    false
                )
            )
        }
    }

    override fun ensureViewType(position: Int, data: List<ProfileAlbum>): Int {
        return if (position == 0) ADD_ALBUM else ALBUM
    }

    override fun onBindItemData(
        position: Int,
        item: ProfileAlbum?,
        holder: MultiHolder<out ViewBinding>
    ) {
        if (getItemViewType(position) == ADD_ALBUM) {
            holder.binding.root.setOnClickListener {
                openAlbum.invoke()
            }
        } else if (getItemViewType(position) == ALBUM) {
            val albumBinding = holder.binding as ItemProfileAlbumBinding
            if (item?.isVideo == true) {
                albumBinding.videoPlayBtn.visible()
                if (!item.videoCover.isNullOrEmpty()) {
                    albumBinding.roundedImg.loadImage(
                        item.videoCover!!,
                        roundedCorners = 20f.dpToPx(context)
                    )
                } else {
                    albumBinding.roundedImg.loadVideo(
                        item.imageUrl,
                        roundedCorners = 20f.dpToPx(context)
                    )
                }

            } else {
                albumBinding.videoPlayBtn.gone()
                albumBinding.roundedImg.loadImage(
                    item?.imageUrl!!,
                    roundedCorners = 20f.dpToPx(context),
                    placeholderRes = com.amigo.uibase.R.drawable.img_placehoder
                )
            }
            albumBinding.deleteBtn.setOnClickListener {
                deleteAlbum.invoke(item)
            }
            albumBinding.videoPlayBtn.setOnClickListener {
                playVideo.invoke(item)
            }
            albumBinding.roundedImg.setOnClickListener {
                showAlbum.invoke(item)
            }
        }
    }

    class ProfileAlbumHolder(view: View) : MultiHolder<ItemProfileAlbumBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemProfileAlbumBinding {
            return ItemProfileAlbumBinding.bind(itemView)
        }
    }

    class AddProfileAlbumHolder(view: View) :
        MultiHolder<ItemProfileAlbumAddBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemProfileAlbumAddBinding {
            return ItemProfileAlbumAddBinding.bind(itemView)
        }
    }

    fun setAlbumAction(
        openAlbum: () -> Unit,
        deleteAlbum: (ProfileAlbum) -> Unit,
        showAlbum: (ProfileAlbum) -> Unit,
        playVideo: (ProfileAlbum) -> Unit
    ) {
        this.openAlbum = openAlbum
        this.deleteAlbum = deleteAlbum
        this.showAlbum = showAlbum
        this.playVideo = playVideo
    }
}