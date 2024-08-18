package com.amigo.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amigo.analysis.Analysis
import com.amigo.basic.recycler.BaseRvAdapter
import com.amigo.basic.recycler.BaseRvHolder
import com.amigo.logic.http.response.pay.Payment
import com.amigo.picture.loadImage
import com.amigo.store.databinding.ItemSettlementStoreBinding
import com.amigo.tool.dpToPx
import com.amigo.uibase.userbehavior.UserBehavior

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