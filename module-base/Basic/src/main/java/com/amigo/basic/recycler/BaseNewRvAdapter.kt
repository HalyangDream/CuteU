package com.amigo.basic.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseNewRvAdapter<T, V : ViewBinding>(val mContext: Context) :
    RecyclerView.Adapter<BaseRvHolder<V>>() {

    private val mData = mutableListOf<T>()

    val items = mData

    private val _layoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    fun setData(list: MutableList<T>?) {
        if (list != null) {
            mData.clear()
            mData.addAll(list)
            notifyItemRangeInserted(0, itemCount)
        }
    }

    fun addData(list: MutableList<T>?) {
        if (list.isNullOrEmpty()) return
        mData.addAll(list)
        val oldSize = items.size
        if (mData.addAll(list)) {
            notifyItemRangeInserted(oldSize, list.size)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRvHolder<V> {

        return bindViewBinding(mContext, parent, viewType, _layoutInflater)
    }

    override fun onBindViewHolder(holder: BaseRvHolder<V>, position: Int) {
        bindData(position, holder.itemBinding, mData[position])
    }

    abstract fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<V>

    abstract fun bindData(position: Int, binding: V, item: T)

}