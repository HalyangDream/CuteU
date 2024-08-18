package com.amigo.picture.media

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.amigo.picture.R
import com.amigo.picture.loadImage

internal class MediaAdapter(val context: Context, private val limit: Int = 9) :
    RecyclerView.Adapter<MediaAdapter.MediaHolder>() {

    private val _layoutInflater = LayoutInflater.from(context)
    private val _originData = mutableListOf<LocalMedia>()
    private val _data = mutableListOf<LocalMedia>()
    private val _selectData = mutableListOf<LocalMedia>()

    private val _itemWidth by lazy { getImageSize() }


    fun addOriginData(list: MutableList<LocalMedia>) {
        _originData.clear()
        _originData.addAll(list)
    }

    fun loadAllData() {
        _data.clear()
        _data.addAll(_originData)
        notifyItemRangeChanged(0,itemCount)
    }

    fun loadAlbumData(album: String) {
        val bucketData = _originData.filter { it.bucket == album }
        _data.clear()
        _data.addAll(bucketData)
        notifyItemRangeChanged(0,itemCount)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {

        val itemView = _layoutInflater.inflate(R.layout.item_media, parent, false)
        return MediaHolder(itemView)

    }

    override fun onBindViewHolder(holder: MediaHolder, position: Int) {
        val item = _data[position]
        val layoutParam = holder.ivMedia.layoutParams
        layoutParam.width = _itemWidth
        layoutParam.height = _itemWidth
        if (_selectData.contains(item)) {
            holder.ivSelect.setImageResource(R.drawable.ic_media_select_sel)
        } else {
            holder.ivSelect.setImageResource(R.drawable.ic_media_select_def)
        }
        if (item.thumbBitmap != null) {
            holder.ivMedia.loadImage(item.thumbBitmap)
        } else {
            holder.ivMedia.loadImage(item.path)
        }
        holder.ivSelect.setOnClickListener {
            if (_selectData.size < limit) {
                if (_selectData.contains(item)) {
                    _selectData.remove(item)
                    holder.ivSelect.setImageResource(R.drawable.ic_media_select_def)
                } else {
                    _selectData.add(item)
                    holder.ivSelect.setImageResource(R.drawable.ic_media_select_sel)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return _data.size
    }

    private fun getImageSize(): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return metrics.widthPixels / 3
    }

    inner class MediaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMedia: ImageView = itemView.findViewById(R.id.iv_media)
        val ivSelect: ImageView = itemView.findViewById(R.id.iv_media_select)
    }

}