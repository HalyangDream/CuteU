package com.cute.mine.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cute.logic.http.response.profile.ProfileTag
import com.cute.mine.R
import com.cute.mine.databinding.ItemProfileTagBinding
import com.cute.picture.loadImage
import com.cute.uibase.adapter.BaseRvFooterAdapter

class ProfileTagAdapter(context: Context) : BaseRvFooterAdapter<ProfileTag>(context) {

    private var onTagClick: (tag: ProfileTag?) -> Unit = {}

    override fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding> {
        return AnchorTagHolder(
            mLayoutInflater.inflate(
                R.layout.item_profile_tag,
                parent,
                false
            )
        )
    }

    override fun bindMainData(
        position: Int,
        item: ProfileTag?,
        holder: MultiHolder<out ViewBinding>
    ) {
        val itemBind = holder.binding as ItemProfileTagBinding
        item?.apply {
            itemBind.tvTagName.text = tabContent
            itemBind.ivTagIcon.loadImage(tabImg)
            itemBind.ivRoot.shapeDrawableBuilder.setSolidColor(Color.parseColor(tabColor))
                .intoBackground()
        }
    }

    fun setOnTagClick(onTagClick: (tag: ProfileTag?) -> Unit) {
        this.onTagClick = onTagClick
    }

    override fun onMainItemClick(position: Int, view: View) {
        super.onMainItemClick(position, view)
        onTagClick.invoke(getItem(position))
    }

    class AnchorTagHolder(private val view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemProfileTagBinding =
            ItemProfileTagBinding.bind(view)
    }
}