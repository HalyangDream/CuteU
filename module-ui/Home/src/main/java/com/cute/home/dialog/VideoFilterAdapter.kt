package com.cute.home.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cute.baselogic.userDataStore
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.home.databinding.ItemVideoFilterBinding
import com.cute.logic.http.response.list.Filter
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService

class VideoFilterAdapter(context: Context) :
    BaseRvAdapter<Filter, ItemVideoFilterBinding>(context) {

    private var selector: Filter? = null
    private var listener: ((filter: Filter) -> Unit)? = null

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.id.toLong()
    }

    fun setSelectorListener(listener: ((filter: Filter) -> Unit)? = null) {
        this.listener = listener
    }

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemVideoFilterBinding> {

        return BaseRvHolder(ItemVideoFilterBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemVideoFilterBinding, item: Filter) {
        binding.tvName.text = item.content
        binding.checkBox.isChecked = selector != null && selector!!.id == item.id
        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                selector = item
                notifyItemRangeChanged(0, itemCount)
                listener?.invoke(item)

            }
        }
        binding.root.setOnClickListener {
            selector = item
            notifyItemRangeChanged(0, itemCount)
            listener?.invoke(item)
        }
    }
}