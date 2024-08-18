package com.amigo.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.amigo.home.R
import com.amigo.home.databinding.ItemEventFollowedBinding
import com.amigo.logic.http.response.list.MyLike
import com.amigo.picture.loadImage
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.tool.dpToPx
import com.amigo.uibase.adapter.BaseRvFooterAdapter
import com.amigo.uibase.gone
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible

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
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
                blurTransformation = if (item.isBlur) BlurTransformation(context, 25f, 3f) else null
            )
            itemBind.sllMsg.gone()
            itemBind.tvName.gone()
            itemBind.tvCountry.gone()
        } else {
            itemBind.ivAvatar.loadImage(
                item.avatar!!,
                roundedCorners = 12f.dpToPx(context),
                placeholderRes = com.amigo.uibase.R.drawable.img_placehoder,
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