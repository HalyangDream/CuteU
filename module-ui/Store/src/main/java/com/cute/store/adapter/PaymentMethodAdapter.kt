package com.cute.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cute.analysis.Analysis
import com.cute.basic.recycler.BaseRvAdapter
import com.cute.basic.recycler.BaseRvHolder
import com.cute.logic.http.response.pay.Payment
import com.cute.picture.loadImage
import com.cute.store.databinding.ItemSettlementStoreBinding
import com.cute.tool.dpToPx
import com.cute.uibase.userbehavior.UserBehavior

class PaymentMethodAdapter(context: Context) :
    BaseRvAdapter<Payment, ItemSettlementStoreBinding>(context) {

    private var select = 0


    fun getSelectItem(): Payment? {

        return getItem(select)
    }

    override fun bindViewBinding(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): BaseRvHolder<ItemSettlementStoreBinding> {

        return BaseRvHolder(ItemSettlementStoreBinding.inflate(layoutInflater, parent, false))
    }

    override fun bindData(position: Int, binding: ItemSettlementStoreBinding, item: Payment) {
        binding.tvName.text = item.name
        binding.ivPayment.loadImage(item.img)
        binding.cbSelect.isChecked = select == position
        binding.root.setOnClickListener {
            select = position
            notifyItemRangeChanged(0, itemCount)
        }
    }
}