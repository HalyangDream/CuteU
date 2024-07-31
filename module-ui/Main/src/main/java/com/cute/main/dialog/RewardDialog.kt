package com.cute.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cute.basic.dialog.BaseCenterDialog
import com.cute.main.R
import com.cute.main.databinding.DialogAdRewardBinding

class RewardDialog : BaseCenterDialog() {

    private lateinit var binding: DialogAdRewardBinding
    private var rewardType: String? = null
    private var rewardContent: String? = null

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogAdRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.tvRewardContent.text = "$rewardContent"
        when (rewardType) {
            "vip" -> binding.ivRewardContent.setImageResource(R.drawable.img_reward_vip)
            "coin" -> binding.ivRewardContent.setImageResource(R.drawable.img_reward_coins)
            "card" -> binding.ivRewardContent.setImageResource(R.drawable.img_reward_card)
            "multiple" -> binding.ivRewardContent.setImageResource(R.drawable.img_reward_multiple)
        }
    }

    override fun initData() {
        binding.root.postDelayed({
            dismissDialog()
        }, 3000)
    }

    fun setRewardContent(rewardType: String?, rewardContent: String?) {
        this.rewardType = rewardType
        this.rewardContent = rewardContent
    }
}