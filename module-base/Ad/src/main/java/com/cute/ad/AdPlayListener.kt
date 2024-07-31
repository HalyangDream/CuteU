package com.cute.ad

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.rewardvideo.api.ATRewardVideoListener

abstract class AdPlayListener: ATRewardVideoListener, ATInterstitialListener {


    abstract fun onAdLoaded()
    abstract fun onAdFailed()
    abstract fun onAdPlayStart()
    abstract fun onAdPlayEnd()
    abstract fun onAdPlayFailed()
    abstract fun onAdClosed()
    abstract fun onAdPlayClicked()
    abstract fun onAdReward()


    override fun onRewardedVideoAdLoaded() {
        onAdLoaded()
    }

    override fun onRewardedVideoAdFailed(p0: AdError?) {
        onAdFailed()
    }

    override fun onRewardedVideoAdPlayStart(p0: ATAdInfo?) {
        onAdPlayStart()
    }

    override fun onRewardedVideoAdPlayEnd(p0: ATAdInfo?) {
        onAdPlayEnd()
    }

    override fun onRewardedVideoAdPlayFailed(p0: AdError?, p1: ATAdInfo?) {
        onAdPlayFailed()
    }

    override fun onRewardedVideoAdClosed(p0: ATAdInfo?) {
        onAdClosed()
    }

    override fun onRewardedVideoAdPlayClicked(p0: ATAdInfo?) {
        onAdPlayClicked()
    }

    override fun onReward(p0: ATAdInfo?) {
        onAdReward()
    }

    override fun onInterstitialAdLoaded() {
       onAdLoaded()
    }

    override fun onInterstitialAdLoadFail(p0: AdError?) {
       onAdFailed()
    }

    override fun onInterstitialAdClicked(p0: ATAdInfo?) {
       onAdPlayClicked()
    }

    override fun onInterstitialAdShow(p0: ATAdInfo?) {
       onAdPlayStart()
    }

    override fun onInterstitialAdClose(p0: ATAdInfo?) {
       onAdClosed()
    }

    override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
        onAdPlayStart()
    }

    override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
        onAdPlayEnd()
    }

    override fun onInterstitialAdVideoError(p0: AdError?) {
        onAdPlayFailed()
    }
}