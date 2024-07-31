package com.cute.home.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.home.databinding.ItemAnchorInfoBinding
import com.cute.logic.http.response.user.UserBaseInfo
import com.cute.uibase.adapter.BaseRvFooterAdapter
import com.cute.uibase.invisible
import com.cute.uibase.visible

class AnchorInfoAdapter(context: Context) : BaseRvFooterAdapter<UserBaseInfo>(context) {

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return AnchorInfoHolder(
            mLayoutInflater.inflate(
                com.cute.home.R.layout.item_anchor_info,
                parent,
                false
            )
        )
    }

    override fun bindMainData(
        position: Int,
        item: UserBaseInfo?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemAnchorInfoBinding
        itemBind.tvTitleValue.text = "${item?.title}："
        itemBind.tvInfoValue.text = item?.content
        if (position == (getRealItemCount() - 1)) {
            itemBind.vDivider.invisible()
        } else {
            itemBind.vDivider.visible()
        }
    }

    class AnchorInfoHolder(private val view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemAnchorInfoBinding =
            ItemAnchorInfoBinding.bind(view)
    }
}