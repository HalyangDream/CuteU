package com.amigo.basic.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType


abstract class BaseRvAdapter<T : Any, V : ViewBinding> constructor(val mContext: Context) :
    BaseQuickAdapter<T, BaseRvHolder<V>>() {


    private val _layoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    fun setData(list: MutableList<T>?) {
        submitList(list)
    }

    fun addData(list: MutableList<T>?) {
        if (list.isNullOrEmpty()) return
        addAll(list as Collection<T>)
    }


    fun getItemData(position: Int): T {

        return getItem(position) as T
    }


    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseRvHolder<V> {

        return bindViewBinding(context, parent, viewType, _layoutInflater)
    }


    override fun onBindViewHolder(holder: BaseRvHolder<V>, position: Int, item: T?) {
        bindData(position, holder.itemBinding, item!!)
    }

    abstract fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<V>

    abstract fun bindData(position: Int, binding: V, item: T)

}