package com.amigo.home.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.amigo.home.R
import com.amigo.home.databinding.ItemFeedBinding
import com.amigo.logic.http.response.list.Feed
import com.amigo.picture.loadImage
import com.amigo.tool.dpToPx
import com.amigo.uibase.adapter.BaseRvFooterAdapter
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.setOnlineLabelImage
import com.amigo.uibase.setOnlinePointImage

class FeedAdapter(context: Context) : BaseRvFooterAdapter<Feed>(context) {


    fun handleLikeState(uid: Long, isLike: Boolean) {
        for ((index, encounter) in items.withIndex()) {
            if (encounter.id == uid) {
                encounter.isFollow = isLike
                notifyItemRangeChanged(index, 1, "partial update")
                break
            }
        }
    }

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return FeedHolder(
            mLayoutInflater.inflate(
                R.layout.item_feed,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, item: Feed?, payloads: List<Any>
    ) {
        if (payloads.isNotEmpty() && item != null && !isFooterViewType(position)
        ) {
            val multiHolder = holder as MultiHolder<out ViewBinding>
            val itemBind = multiHolder.binding as ItemFeedBinding
//            val imgRes = if (item.isFollow) R.drawable.ic_feed_msg else R.drawable.ic_feed_like
//            itemBind.ivLike.setImageResource(imgRes)
        } else {
            super.onBindViewHolder(holder, position, item, payloads)
        }
    }

    override fun bindMainData(position: Int, item: Feed?, holder: MultiHolder<out ViewBinding>) {
        val itemBind = holder.binding as ItemFeedBinding
        itemBind.tvName.text = item!!.name
        itemBind.ivAvatar.loadImage(
            item.avatar,
            roundedCorners = 12f.dpToPx(context),
            placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
            errorRes = com.amigo.uibase.R.drawable.img_placehoder
        )
        itemBind.ivOnline.setOnlineLabelImage(item.online)
        if (!TextUtils.isEmpty(item.city)) {
            itemBind.tvCountry.text = "${item.city},${item.country}"
        } else {
            itemBind.tvCountry.text = item.country
        }
//        val imgRes = if (item.isFollow) R.drawable.ic_feed_msg else R.drawable.ic_feed_like
//        itemBind.ivLike.setImageResource(imgRes)
    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        val item = items[position]
        RouteSdk.navigationUserDetail(item.id, "anchor_list_details")
    }


    class FeedHolder(private val view: View) : MultiHolder<ItemFeedBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemFeedBinding =
            ItemFeedBinding.bind(itemView)
    }
}