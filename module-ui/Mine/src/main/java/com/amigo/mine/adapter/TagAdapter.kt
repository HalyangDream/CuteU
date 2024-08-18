package com.amigo.mine.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.amigo.basic.recycler.MultiLayoutRvAdapter
import com.amigo.logic.http.response.profile.TagSubItem
import com.amigo.mine.R
import com.amigo.mine.databinding.ItemTagBinding
import com.amigo.mine.databinding.ItemTagTitleBinding
import com.amigo.tool.dpToPx

class TagAdapter(context: Context) : MultiLayoutRvAdapter<TagSubItem>(context) {

    private companion object {
        const val TAG_TITLE = 0
        const val TAG_CONTENT = 1
    }

    override fun itemViewTypes(): IntArray = intArrayOf(TAG_TITLE, TAG_CONTENT)

    override fun fullSpanItem(vieType: Int): Boolean = vieType == TAG_TITLE

    override fun createHolder(vieType: Int, parent: ViewGroup): MultiHolder<out ViewBinding> {
        return when (vieType) {
            TAG_TITLE -> TagTitleHolder(
                mLayoutInflater.inflate(
                    R.layout.item_tag_title,
                    parent,
                    false
                )
            )

            else -> TagContentHolder(
                mLayoutInflater.inflate(
                    R.layout.item_profile_tag,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindItemData(
        position: Int,
        item: TagSubItem?,
        holder: MultiHolder<out ViewBinding>
    ) {
        when (holder) {
            is TagTitleHolder -> {
                val titleBinding = holder.binding as ItemTagTitleBinding
                titleBinding.tvTagTitle.text = item?.name
            }

            is TagContentHolder -> {
                val tagBinding = holder.binding as ItemTagBinding
                item?.apply {
                    tagBinding.tvTagName.text = name
                    if (item.selected) {
                        tagBinding.ivRoot.shapeDrawableBuilder.setSolidColor(
                            ContextCompat.getColor(
                                context,
                                com.amigo.uibase.R.color.app_main_color
                            )
                        )
                            .intoBackground()
                    } else {
                        tagBinding.ivRoot.shapeDrawableBuilder
                            .setStrokeWidth(1.dpToPx(context))
                            .setStrokeColor(
                                ContextCompat.getColor(
                                    context,
                                    com.amigo.uibase.R.color.color_tag_border
                                )
                            )
                            .setSolidColor(
                                ContextCompat.getColor(
                                    context,
                                    com.amigo.uibase.R.color.white
                                )
                            )
                            .intoBackground()
                    }
                    tagBinding.root.setOnClickListener {
                        updateSelected(position)
                    }
                }
            }
        }
    }

    private fun updateSelected(position: Int) {
        val item = getItem(position)
        item?.let {
            it.selected = !it.selected
        }
        notifyItemChanged(position)
    }

    fun getSelectedTags(): List<TagSubItem> {
        val list = mutableListOf<TagSubItem>()
        items.forEach {
            if (it.selected) {
                list.add(it)
            }
        }
        return list
    }


    override fun ensureViewType(position: Int, data: List<TagSubItem>): Int {
        return if (data[position].id == -999) TAG_TITLE else TAG_CONTENT
    }

    class TagTitleHolder(view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ViewBinding {
            return ItemTagTitleBinding.bind(itemView)
        }
    }

    class TagContentHolder(view: View) : MultiHolder<ViewBinding>(view) {
        override fun bindViewBinding(itemView: View): ViewBinding {
            return ItemTagBinding.bind(itemView)
        }
    }
}