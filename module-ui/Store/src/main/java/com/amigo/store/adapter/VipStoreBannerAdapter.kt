package com.amigo.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amigo.logic.http.response.product.VipPowerInfoData
import com.amigo.picture.loadCrossFade
import com.amigo.store.R
import com.amigo.store.databinding.ItemVipStoreBannerBinding
import com.youth.banner.adapter.BannerAdapter

class VipStoreBannerAdapter(context: Context, data: MutableList<VipPowerInfoData>) :
    BannerAdapter<VipPowerInfoData, VipStoreBannerAdapter.VipStoreHolder>(data) {


    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): VipStoreHolder {
        return VipStoreHolder(layoutInflater.inflate(R.layout.item_vip_store_banner, parent, false))
    }

    override fun onBindView(
        holder: VipStoreHolder,
        data: VipPowerInfoData,
        position: Int,
        size: Int
    ) {
        holder.binding.ivBanner.loadCrossFade(data.cover)
    }

    class VipStoreHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemVipStoreBannerBinding.bind(itemView)
    }

}