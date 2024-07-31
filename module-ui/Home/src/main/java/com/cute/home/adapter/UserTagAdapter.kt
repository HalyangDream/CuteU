package com.cute.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.home.R
import com.cute.home.databinding.ItemUserTagBinding
import com.cute.logic.http.response.user.UserTag
import com.cute.picture.loadImage
import com.cute.uibase.adapter.BaseRvFooterAdapter

class UserTagAdapter(context: Context) : BaseRvFooterAdapter<UserTag>(context) {

    private var onTagClick: (tag: UserTag?) -> Unit = {}

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return AnchorTagHolder(
            mLayoutInflater.inflate(
                R.layout.item_user_tag,
                parent,
                false
            )
        )
    }

    override fun bindMainData(
        position: Int,
        item: UserTag?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemUserTagBinding
        item?.apply {
            itemBind.tvTagName.text = tabContent
            itemBind.ivTagIcon.loadImage(tabImg)
            itemBind.ivRoot.shapeDrawableBuilder.setSolidColor(Color.parseColor(tabColor))
                .intoBackground()
        }
    }

    fun setOnTagClick(onTagClick: (tag: UserTag?) -> Unit) {
        this.onTagClick = onTagClick
    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        onTagClick.invoke(getItem(position))
    }

    class AnchorTagHolder(private val view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemUserTagBinding =
            ItemUserTagBinding.bind(view)
    }
}