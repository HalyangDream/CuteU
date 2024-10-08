package com.amigo.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amigo.logic.http.response.product.VipPowerInfoData
import com.amigo.mine.R
import com.amigo.mine.databinding.ItemVipBannerDescBinding
import com.amigo.picture.loadImage
import com.youth.banner.adapter.BannerAdapter

class BannerVipDescAdapter(val context: Context, data: MutableList<VipPowerInfoData>) :
    BannerAdapter<VipPowerInfoData, BannerVipDescAdapter.BannerVipTextHolder>(data) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerVipTextHolder {
        return BannerVipTextHolder(
            layoutInflater.inflate(
                R.layout.item_vip_banner_desc,
                parent,
                false
            )
        )
    }

    override fun onBindView(
        holder: BannerVipTextHolder,
        data: VipPowerInfoData,
        position: Int,
        size: Int
    ) {
        holder.binding.tvTitle.text = "${data.title}"
    }

    class BannerVipTextHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemVipBannerDescBinding.bind(itemView)
    }
}