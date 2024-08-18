package com.amigo.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.amigo.ad.AdAdaptPlayExternalListener
import com.amigo.basic.dialog.BaseCenterDialog
import com.amigo.logic.http.model.AdRepository
import com.amigo.logic.http.response.ad.RewardVideoAdReward
import com.amigo.logic.http.response.ad.RewardVideoAdRewardInfo
import com.amigo.main.databinding.DialogLookRewardVideoAdCoinBinding
import com.amigo.picture.loadImage
import com.amigo.tool.EventBus
import com.amigo.tool.Toaster
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.event.GetRewardEvent
import kotlinx.coroutines.launch

class LookRewardVideoAdCoinDialog : BaseCenterDialog() {

    private lateinit var binding: DialogLookRewardVideoAdCoinBinding
    private val adRepository = AdRepository()
    private var rewardVideoAdRewardInfo: RewardVideoAdRewardInfo? = null
    private var hasGetReward = false

    override fun setDialogWidthRate(): Float {
        return -2f
    }

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogLookRewardVideoAdCoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        binding.ivClose.setOnClickListener {
            dismissDialog()
        }
        binding.flWatchAd.setOnClickListener {
            playRewardAd()
        }
        binding.flWatchAd.isEnabled = false
        binding.flWatchAd.alpha = 0.3f
    }

    override fun initData() {
        lifecycleScope.launch {
            val response = adRepository.getRewardVideoInfo("coin")
            rewardVideoAdRewardInfo = response.data
            bindData()
        }
    }

    private fun bindData() {
        if (rewardVideoAdRewardInfo == null) {
            binding.flWatchAd.isEnabled = false
            binding.flWatchAd.alpha = 0.3f
            return
        }

        binding.ivReward.loadImage(rewardVideoAdRewardInfo!!.rewardImg)
        binding.tvReward.text = rewardVideoAdRewardInfo!!.reward
        val progress = rewardVideoAdRewardInfo!!.progress
        val totalProgress = rewardVideoAdRewardInfo!!.totalProgress
        binding.tvProgress.text = "$progress/$totalProgress"
        val progressBar = (progress.toFloat() / totalProgress) * 100
        binding.progressBar.progress = progressBar.toInt()
        binding.flWatchAd.isEnabled = true
        binding.flWatchAd.alpha = 1f
    }

    private fun playRewardAd() {
        if (rewardVideoAdRewardInfo == null) return
        if (activity == null) return
        if (rewardVideoAdRewardInfo!!.isPlayAd) {
            //播完之后再次重新请求

            val result = AdPlayService.loadRewardAdVideo(object : AdAdaptPlayExternalListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                    if (hasGetReward) {
                        hasGetReward = false
                        reportPlayComplete()
                    }

                }

                override fun onAdReward() {
                    super.onAdReward()
                    hasGetReward = true
                }
            })
            if (result) {
                rewardVideoAdRewardInfo = null
                binding.flWatchAd.isEnabled = false
                binding.flWatchAd.alpha = 0.3f
            } else {
                context?.let {
                    Toaster.showShort(
                        it,
                        com.amigo.uibase.R.string.str_can_not_play_ad
                    )
                }
            }
        } else {
            context?.let { Toaster.showShort(it, "${rewardVideoAdRewardInfo?.toastMessage}") }
        }
    }

    private fun reportPlayComplete() {
        lifecycleScope.launch {
            val response = adRepository.playRewardVideoComplete("coin")
            val rewardVideoAdReward = response.data
            if (rewardVideoAdReward != null) {
                showAdRewardDialog(rewardVideoAdReward)
            }
            initData()
        }
    }

    private fun showAdRewardDialog(data: RewardVideoAdReward) {
        if (data.isGiveReward) {
            EventBus.post(GetRewardEvent.CoinAdReward)
            context?.let {
                val dialog = RewardDialog()
                dialog.setRewardContent(data.rewardType, data.rewardContent!!)
                dialog.showDialog(context, null)
            }
            dismissDialog()
        }
    }
}