package com.cute.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.chat.databinding.ItemChatHeaderAdapterBinding
import com.cute.logic.http.response.user.ChatUserInfo
import com.cute.picture.loadImage
import com.cute.uibase.route.RouteSdk

class ChatHeaderAdapter(context: Context) :
    BaseRvAdapter<ChatUserInfo, ItemChatHeaderAdapterBinding>(context) {


    override fun getItemCount(items: List<ChatUserInfo>): Int {
        return items.size
    }

    override fun bindViewBinding(
        context: Context, parent: ViewGroup, viewType: Int, layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemChatHeaderAdapterBinding> {

        return BaseRvHolder(ItemChatHeaderAdapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(
        position: Int, binding: ItemChatHeaderAdapterBinding, item: ChatUserInfo
    ) {

        binding.ivAvatar.loadImage(
            item.avatar,
            isCircle = true,
            placeholderRes = com.cute.uibase.R.drawable.img_placehoder_round_grey
        )
        binding.tvName.text = item.name
        val cityAndCountry = if (item.city.isNotEmpty() && item.country.isNotEmpty()) {
            "${item.city},${item.country}"
        } else {
            "${item.city}${item.country}"
        }
        binding.tvCountryCity.text = cityAndCountry
        val albumAdapter = ChatHeaderAlbumAdapter(context)
        albumAdapter.setData(item.album)
        binding.rvAlbum.adapter = albumAdapter
        binding.ivAvatar.setOnClickListener {
            RouteSdk.navigationUserDetail(item.id, "msg_page")
        }
        binding.tvName.setOnClickListener {
            RouteSdk.navigationUserDetail(item.id, "msg_page")
        }
        binding.tvCountryCity.setOnClickListener {
            RouteSdk.navigationUserDetail(item.id, "msg_page")
        }
    }
}