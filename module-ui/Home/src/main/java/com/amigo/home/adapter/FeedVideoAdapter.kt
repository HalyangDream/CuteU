package com.amigo.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.home.R
import com.amigo.home.databinding.ItemFeedVideoBinding
import com.amigo.logic.http.response.list.VideoList
import com.amigo.picture.loadImage
import com.amigo.tool.dpToPx
import com.amigo.uibase.invisible
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.setOnlinePointImage
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible

class FeedVideoAdapter(context: Context) : BaseRvAdapter<VideoList, ItemFeedVideoBinding>(context) {

    private val telephoneService = RouteSdk.findService(ITelephoneService::class.java)


    fun handleLikeState(uid: Long, isFollow: Boolean) {
        for ((index, item) in items.withIndex()) {
            if (item.id == uid) {
                item.isFollow = isFollow
                item.followNum = if (isFollow) item.followNum + 1 else item.followNum - 1
                notifyItemRangeChanged(index, 1, "partial update")
            }
        }
    }

    fun clear() {
        if (items is ArrayList) {
            (items as? ArrayList)?.clear()
        }

        if (items is MutableList) {
            (items as? MutableList)?.clear()
        }
        notifyDataSetChanged()
    }

    override fun bindViewBinding(
        context: Context, parent: ViewGroup, viewType: Int, layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemFeedVideoBinding> {
        return BaseRvHolder(ItemFeedVideoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: BaseRvHolder<ItemFeedVideoBinding>,
        position: Int,
        item: VideoList?,
        payloads: List<Any>
    ) {

        if (payloads.isNotEmpty() && item != null) {
            if (item.isFollow) {
                holder.itemBinding.ivLikeState.setImageResource(R.drawable.ic_feed_video_liked)
            } else {
                holder.itemBinding.ivLikeState.setImageResource(R.drawable.ic_feed_video_unliked)
            }
            holder.itemBinding.tvLike.text = item.followNum.toString()
        } else {
            super.onBindViewHolder(holder, position, item, payloads)
        }
    }

    override fun bindData(position: Int, binding: ItemFeedVideoBinding, item: VideoList) {
        binding.ivAvatar.loadImage(
            item.avatar,
            isCircle = true,
            borderColor = com.amigo.uibase.R.color.white,
            borderWidthPx = 1.dpToPx(context)
        )
        binding.tvName.text = "${item.name}, ${item.age}"
        binding.tvLike.text = item.followNum.toString()
        binding.tvPrice.text = item.callPrice
        binding.ivOnline.setOnlinePointImage(item.online)
        binding.tvLocation.text = item.location
        binding.videoView.setVideoData(item.videoUrl, item.videoCover)
        if (item.callPrice.isEmpty()) {
            binding.tvPrice.invisible()
        } else {
            binding.tvPrice.visible()
        }
        if (item.isFollow) {
            binding.ivLikeState.setImageResource(R.drawable.ic_feed_video_liked)
        } else {
            binding.ivLikeState.setImageResource(R.drawable.ic_feed_video_unliked)
        }
        binding.llCall.setOnClickListener {
            telephoneService.sendCallInvited(
                context, UserDataStore.get(context).getUid(), item.id, "show_list"
            )
            UserBehavior.setChargeSource("show_list_call")
        }

        binding.llMessage.setOnClickListener {
            RouteSdk.navigationChat(item.id, "show_list")
        }
        binding.ivAvatar.setOnClickListener {
            RouteSdk.navigationUserDetail(item.id, "show_list")
        }
    }
}