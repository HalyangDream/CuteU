package com.cute.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.home.R
import com.cute.home.databinding.ItemEventFollowedBinding
import com.cute.logic.http.response.list.MyLike
import com.cute.picture.loadImage
import com.cute.picture.transformation.BlurTransformation
import com.cute.tool.dpToPx
import com.cute.uibase.adapter.BaseRvFooterAdapter
import com.cute.uibase.gone
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible

class EventLikeMeAdapter(context: Context) : BaseRvFooterAdapter<MyLike>(context) {


    override fun bindMainData(position: Int, item: MyLike?, holder: MultiHolder<out ViewBinding>) {
        val itemBind = holder.binding as ItemEventFollowedBinding
        itemBind.tvName.text = item?.name
        if (!item?.city.isNullOrEmpty()) {
            itemBind.tvCountry.text = "${item?.city},${item?.country}"
        } else {
            itemBind.tvCountry.text = item?.country
        }
        if (item!!.isBlur) {
            itemBind.ivAvatar.loadImage(
                item.avatar!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
                blurTransformation = if (item.isBlur) BlurTransformation(context, 25f, 3f) else null
            )
            itemBind.sllMsg.gone()
            itemBind.tvName.gone()
            itemBind.tvCountry.gone()
        } else {
            itemBind.ivAvatar.loadImage(
                item.avatar!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.cute.uibase.R.drawable.img_placehoder,
            )
            itemBind.sllMsg.visible()
            itemBind.tvName.visible()
            itemBind.tvCountry.visible()
        }
        itemBind.sllMsg.setOnClickListener {
            if (item.isBlur) {
                RouteSdk.navigationVipStore()
                UserBehavior.setChargeSource("like_list")
            } else {
                RouteSdk.navigationChat(item.id, "like_list")
            }
        }
    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        val item = getItem(position) ?: return
        if (item.isBlur) {
            RouteSdk.navigationVipStore()
            UserBehavior.setChargeSource("like_list")
        } else {
            RouteSdk.navigationChat(item.id, "like_list")
        }
    }

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return EventLikeHolder(mLayoutInflater.inflate(R.layout.item_event_followed, parent, false))
    }


    class EventLikeHolder(private val view: View) : MultiHolder<ItemEventFollowedBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemEventFollowedBinding =
            ItemEventFollowedBinding.bind(itemView)
    }
}