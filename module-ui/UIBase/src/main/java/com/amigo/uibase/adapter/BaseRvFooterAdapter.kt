package com.amigo.uibase.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.amigo.basic.recycler.MultiLayoutRvAdapter
import com.amigo.uibase.databinding.ItemRecyclerFooterBinding
import com.amigo.uibase.R

abstract class BaseRvFooterAdapter<T : Any>(context: Context) : MultiLayoutRvAdapter<T>(context) {

    private var footerIsShow = false

    companion object {
        private const val SELECTION = 0
        private const val FOOTER = 1
    }

    abstract fun createMainHolder(parent: ViewGroup): MultiHolder<out ViewBinding>

    abstract fun bindMainData(position: Int, item: T?, holder: MultiHolder<out ViewBinding>)

    open fun onMainItemClick(position: Int, view: View) {

    }

    open fun onMainItemLongClick(position: Int, view: View) {

    }

    open fun setData(list: MutableList<T>?) {
        submitList(list)
    }

    open fun addData(list: MutableList<T>?) {
        if (list.isNullOrEmpty()) return
        addAll(list as Collection<T>)
    }


    fun getItemData(position: Int): T {

        return getItem(position) as T
    }

    fun showFooter(showFooter: Boolean) {
        if (showFooter) {
            if (!footerIsShow) {
                footerIsShow = true
                notifyItemInserted(itemCount)
            }
        } else {
            if (footerIsShow) {
                footerIsShow = false
                notifyItemRemoved(itemCount)
            }
        }
    }


    override fun itemViewTypes(): IntArray = intArrayOf(SELECTION, FOOTER)

    override fun createHolder(vieType: Int, parent: ViewGroup): MultiHolder<out ViewBinding> {
        return if (vieType == FOOTER) {
            FooterHolder(
                mLayoutInflater.inflate(
                    R.layout.item_recycler_footer,
                    parent,
                    false
                )
            )
        } else createMainHolder(parent)
    }

    override fun onBindItemData(position: Int, item: T?, holder: MultiHolder<out ViewBinding>) {
        if (getItemViewType(position) == SELECTION) {
            bindMainData(position, item, holder)
            holder.binding.root.setOnClickListener {
                onMainItemClick(position, it)
            }
            holder.binding.root.setOnLongClickListener {
                onMainItemLongClick(position, it)
                false
            }
        }
    }

    override fun onItemChildClick(v: View, position: Int) {
        if (getItemViewType(position) == SELECTION) {
            super.onItemChildClick(v, position)
        }
    }

    override fun onItemChildLongClick(v: View, position: Int): Boolean {
        if (getItemViewType(position) == SELECTION) {
            return super.onItemChildLongClick(v, position)
        }
        return false
    }


    override fun ensureViewType(position: Int, data: List<T>): Int {
        if (position >= data.size && footerIsShow) {
            return FOOTER
        }
        return SELECTION
    }

    override fun fullSpanItem(vieType: Int): Boolean {
        return vieType == FOOTER
    }

    override fun getItemCount(items: List<T>): Int {
        if (footerIsShow) {
            return super.getItemCount(items) + 1
        }
        return super.getItemCount(items)
    }

    fun getRealItemCount(): Int {
        return items.size
    }

    fun isFooterViewType(position: Int): Boolean {
        return getItemViewType(position) == FOOTER
    }


    private class FooterHolder(view: View) : MultiHolder<ItemRecyclerFooterBinding>(view) {
        override fun bindViewBinding(itemView: View): ItemRecyclerFooterBinding =
            ItemRecyclerFooterBinding.bind(itemView)
    }

}