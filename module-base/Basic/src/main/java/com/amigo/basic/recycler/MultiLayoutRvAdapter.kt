package com.amigo.basic.recycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseMultiItemAdapter
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

abstract class MultiLayoutRvAdapter<T : Any>(context: Context) :
    BaseMultiItemAdapter<T>() {

    private val _layoutInflater = LayoutInflater.from(context)

    val mLayoutInflater = _layoutInflater


    init {
        for (itemViewType in itemViewTypes()) {
            addItemType(itemViewType,
                object : OnMultiItemAdapterListener<T, MultiHolder<out ViewBinding>> {
                    override fun onBind(
                        holder: MultiHolder<out ViewBinding>,
                        position: Int,
                        item: T?
                    ) {
                        onBindItemData(position, item, holder)
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): MultiHolder<out ViewBinding> {
                        return createHolder(viewType, parent)
                    }

                    override fun isFullSpanItem(itemType: Int): Boolean {
                        return fullSpanItem(itemType)
                    }
                })
        }


        this.onItemViewType(OnItemViewTypeListener<T> { position, list ->
            ensureViewType(position, list)
        })
    }


    abstract fun itemViewTypes(): IntArray

    abstract fun ensureViewType(position: Int, data: List<T>): Int

    abstract fun fullSpanItem(vieType: Int): Boolean

    abstract fun createHolder(vieType: Int, parent: ViewGroup): MultiHolder<out ViewBinding>


    abstract fun onBindItemData(position: Int, item: T?, holder: MultiHolder<out ViewBinding>)


    abstract class MultiHolder<V : ViewBinding>(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        abstract fun bindViewBinding(itemView: View): V

        val binding: V
            get() = bindViewBinding(itemView)

    }

}