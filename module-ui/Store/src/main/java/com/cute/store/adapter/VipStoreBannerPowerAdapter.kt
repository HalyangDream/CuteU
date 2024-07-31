package com.cute.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cute.logic.http.response.product.VipPowerInfoData
import com.cute.picture.loadImage
import com.cute.store.R
import com.cute.store.databinding.ItemVipStoreBannerPowerBinding
import com.youth.banner.adapter.BannerAdapter

class VipStoreBannerPowerAdapter(context: Context, data: MutableList<VipPowerInfoData>) :
    BannerAdapter<VipPowerInfoData, VipStoreBannerPowerAdapter.VipStoreHolder>(data) {


    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): VipStoreHolder {
        return VipStoreHolder(
            layoutInflater.inflate(
                R.layout.item_vip_store_banner_power,
                parent,
                false
            )
        )
    }

    override fun onBindView(holder: VipStoreHolder, data: VipPowerInfoData, position: Int, size: Int) {
        holder.binding.ivIcon.loadImage(data.smallCover)
        holder.binding.tvTitle.text = "${data.title}"
        holder.binding.tvContent.text = "${data.content}"
    }

    class VipStoreHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemVipStoreBannerPowerBinding.bind(itemView)
    }

}