package com.amigo.basic.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseRvHolder<V : ViewBinding>(val itemBinding: V) :
    RecyclerView.ViewHolder(itemBinding.root) {

}