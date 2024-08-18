package com.amigo.ad

import android.app.Activity
import android.app.Application.getProcessName
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener
import com.anythink.network.mintegral.MintegralATConst.DEBUGGER_CONFIG.Mintegral_NATIVE_TEMPLATE
import com.anythink.network.mintegral.MintegralATConst.DEBUGGER_CONFIG.Mintegral_NETWORK
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdExtraInfo
import com.anythink.splashad.api.ATSplashAdListener


object AdFactory {

    private val cacheSplashAds = mutableMapOf<String, ATSplashAd>()
    fun createFactory(
        context: Context,
        isDebugMode: Boolean,
        topOnAppId: String,
        topOnAppKey: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            if (!context.packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
        ATSDK.init(context, topOnAppId, topOnAppKey)
        if (isDebugMode) {
            ATSDK.setNetworkLogDebug(true)
            ATSDK.integrationChecking(context)
        }
    }

    fun getAdDeviceId(context: Context, listener: ((String) -> Unit)?) {
        ATSDK.testModeDeviceInfo(context) {
            listener?.invoke(it)
        }
    }

    fun setDebugMode(context: Context, deviceId: String) {
        val config = ATDebuggerConfig.Builder(Mintegral_NETWORK)
            .setNativeType(Mintegral_NATIVE_TEMPLATE)
            .build()

        ATSDK.setDebuggerConfig(context, deviceId, config)
    }

    fun cacheRewardAdVideo(context: Context, placementIds: Array<String>) {
        try {
            ATRewardVideoAutoAd.init(context, placementIds, object : ATRewardVideoAutoLoadListener {
                override fun onRewardVideoAutoLoaded(p0: String?) {
                    Log.i("AdFactory", "cacheRewardAdVideo onRewardVideoAutoLoaded:$p0")
                }

                override fun onRewardVideoAutoLoadFail(p0: String?, p1: AdError?) {
                    Log.i("AdFactory", "cacheRewardAdVideo onRewardVideoAutoLoadFail:$p0")
                    Log.i(
                        "AdFactory",
                        "cacheRewardAdVideo onRewardVideoAutoLoadFail error:${p1?.fullErrorInfo}"
                    )
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun cacheInterstitial(context: Context, placementIds: Array<String>) {
        try {
            ATInterstitialAutoAd.init(
                context,
                placementIds,
                object : ATInterstitialAutoLoadListener {

                    override fun onInterstitialAutoLoaded(p0: String?) {
                        Log.i("AdFactory", "cacheInterstitial onInterstitialAutoLoaded:$p0")
                    }

                    override fun onInterstitialAutoLoadFail(p0: String?, p1: AdError?) {
                        Log.i("AdFactory", "cacheInterstitial onInterstitialAutoLoadFail:$p0")
                        Log.i(
                            "AdFactory",
                            "cacheRewardAdVideo cacheInterstitial error:${p1?.fullErrorInfo}"
                        )
                    }
                })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    /**
     * 缓存开屏广告
     */
    fun cacheSplashAd(context: Context, placementIds: Array<String>, isReloadAll: Boolean) {
        if (isReloadAll) {
            cacheSplashAds.clear()
        }
        for (placementId in placementIds) {
            val ad = ATSplashAd(context, placementId, null)
            ad.loadAd()
            cacheSplashAds[placementId] = ad
        }
    }

    /**
     * @return true:有广告，false:没有广告
     */
    fun loadRewardAdVideo(
        activity: Activity,
        placementId: String,
        listener: AdPlayExternalListener? = null
    ): Boolean {
        ATRewardVideoAd.entryAdScenario(placementId, null)
        val ad = ATRewardVideoAd(activity, placementId)
        ad.setAdListener(object : AdPlayListener() {
            override fun onAdLoaded() {
                listener?.onAdLoaded()
            }

            override fun onAdFailed() {
                listener?.onAdFailed()
            }

            override fun onAdPlayStart() {
                listener?.onAdPlayStart()
            }

            override fun onAdPlayEnd() {
                listener?.onAdPlayEnd()
            }

            override fun onAdPlayFailed() {
                listener?.onAdPlayFailed()
            }

            override fun onAdClosed() {
                listener?.onAdClosed()
            }

            override fun onAdPlayClicked() {
                listener?.onAdPlayClicked()
            }

            override fun onAdReward() {
                listener?.onAdReward()
            }
        })
        if (ad.isAdReady) {
            ad.show(activity)
            return true
        }
        return false
    }

    fun loadInterstitial(
        activity: Activity,
        placementId: String,
        listener: AdPlayExternalListener? = null
    ): Boolean {
        ATInterstitial.entryAdScenario(placementId, null)
        val ad = ATInterstitial(activity, placementId)
        ad.setAdListener(object : AdPlayListener() {
            override fun onAdLoaded() {
                listener?.onAdLoaded()
            }

            override fun onAdFailed() {
                listener?.onAdFailed()
            }

            override fun onAdPlayStart() {
                listener?.onAdPlayStart()
            }

            override fun onAdPlayEnd() {
                listener?.onAdPlayEnd()
            }

            override fun onAdPlayFailed() {
                listener?.onAdPlayFailed()
            }

            override fun onAdClosed() {
                listener?.onAdClosed()
            }

            override fun onAdPlayClicked() {
                listener?.onAdPlayClicked()
            }

            override fun onAdReward() {
                listener?.onAdReward()
            }
        })
        if (ad.isAdReady) {
            ad.show(activity)
            return true
        }
        return false
    }


    fun findEcpmMaxAdIdForRewardVideoAd(adIds: MutableList<String>?): String? {
        if (adIds.isNullOrEmpty()) return null

        val atRewardAdInfo = adIds.map { ATRewardVideoAutoAd.checkAdStatus(it) }
        val atRewardAd = atRewardAdInfo.filter { it.isReady }.maxByOrNull { it.atTopAdInfo.ecpm }
            ?: return null
        return atRewardAd.atTopAdInfo.placementId
    }

    fun findEcpmMaxAdIdForInterstitialAd(adIds: MutableList<String>?): String? {
        if (adIds.isNullOrEmpty()) return null
        val atInterstitialAdInfo = adIds.map { ATInterstitialAutoAd.checkAdStatus(it) }
        val atInterstitialAd =
            atInterstitialAdInfo.filter { it.isReady }.maxByOrNull { it.atTopAdInfo.ecpm }
                ?: return null
        return atInterstitialAd.atTopAdInfo.placementId
    }

    fun findEcpmMaxAdByRewardAndInters(
        rewardIds: MutableList<String>?, interIds: MutableList<String>?
    ): String? {

        val rewardAdId = findEcpmMaxAdIdForRewardVideoAd(rewardIds)
        val interAdId = findEcpmMaxAdIdForInterstitialAd(interIds)
        if (rewardAdId != null && interAdId != null) {
            val atRewardAdInfo = ATRewardVideoAutoAd.checkAdStatus(rewardAdId)
            val atInterstitialAdInfo = ATInterstitialAutoAd.checkAdStatus(interAdId)
            val rewardEcpm = atRewardAdInfo.atTopAdInfo.ecpm
            val interEcpm = atInterstitialAdInfo.atTopAdInfo.ecpm
            if (interEcpm > rewardEcpm) interAdId else rewardAdId

        }
        return interAdId ?: rewardAdId
    }

//    fun goToAdPage(context: Context) {
//        ATDebuggerUITest.showDebuggerUI(context)
//    }

    fun loadSplashAd(
        activity: Activity, container: ViewGroup,
        goStepListener: (() -> Unit)? = null,
        onAdShow: (() -> Unit)? = null,
    ) {
        val adSplashEntry = cacheSplashAds.filter { it.value.isAdReady }
            .maxByOrNull { it.value.checkAdStatus().atTopAdInfo.ecpm }
        if (adSplashEntry == null) {
            cacheSplashAd(activity, cacheSplashAds.keys.toTypedArray(), false)
            goStepListener?.invoke()
            return
        }
        val adSplashAd = adSplashEntry.value
        adSplashAd.setAdListener(object : ATSplashAdListener {
            override fun onAdLoaded(isTimeOut: Boolean) {

            }

            override fun onAdLoadTimeout() {
                goStepListener?.invoke()
            }

            override fun onNoAdError(p0: AdError?) {
                goStepListener?.invoke()
            }

            override fun onAdShow(p0: ATAdInfo) {
                onAdShow?.invoke()
            }

            override fun onAdClick(p0: ATAdInfo?) {

            }

            override fun onAdDismiss(p0: ATAdInfo, p1: ATSplashAdExtraInfo?) {
                cacheSplashAds.remove(p0.placementId)
                cacheSplashAd(activity, arrayOf(p0.placementId), false)
                goStepListener?.invoke()
            }
        })
        adSplashAd.show(activity, container)
    }

}