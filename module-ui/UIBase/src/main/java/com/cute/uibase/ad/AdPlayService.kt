package com.cute.uibase.ad

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cute.ad.AdAdaptPlayExternalListener
import com.cute.ad.AdFactory
import com.cute.ad.AdPlayExternalListener
import com.cute.baselogic.deviceDataStore
import com.cute.logic.http.model.AdRepository
import com.cute.tool.dpToPx
import com.cute.uibase.ActivityStack
import com.cute.uibase.R
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService
import com.cute.uibase.userbehavior.UserBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object AdPlayService {

    private val adRepository = AdRepository()
    private var rewardVideoIds: MutableList<String>? = null
    private var interstitialAdIds: MutableList<String>? = null
    private var splashIds: MutableList<String>? = null
    private var adScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val telephoneService by lazy { RouteSdk.findService(ITelephoneService::class.java) }

    private var weakReference: WeakReference<AdPlayExternalListener>? = null

    fun cacheAd(context: Context) {
        adScope.launch {
            val response = adRepository.getAdConfig()
            rewardVideoIds = response.data?.rewardAdIds
            interstitialAdIds = response.data?.interstitialAdIds
            splashIds = response.data?.splashIds
            if (!rewardVideoIds.isNullOrEmpty()) {
                AdFactory.cacheRewardAdVideo(context, rewardVideoIds!!.toTypedArray())
            }
            if (!interstitialAdIds.isNullOrEmpty()) {
                AdFactory.cacheInterstitial(context, interstitialAdIds!!.toTypedArray())
            }
            if (!splashIds.isNullOrEmpty()) {
                context.deviceDataStore.saveSplashIds(splashIds!!.toSet())
                AdFactory.cacheSplashAd(context, splashIds!!.toTypedArray(), true)
            }
        }
    }

    /**
     * 上报插屏广告播放场景
     */
    fun reportPlayAdScenes(scenes: String, code: String? = null) {
        adScope.launch {
            val response = adRepository.reportPlayAdScenes(scenes, code)
            if (response.isSuccess && response.data?.isPlayAd == true) {
                //播放插屏广告
                loadInterstitialAd()
            }
        }
    }

    fun loadRewardAdVideo(listener: AdPlayExternalListener): Boolean {
        weakReference = null
        weakReference = WeakReference(listener)
        if (rewardVideoIds.isNullOrEmpty()) return false
        if (ActivityStack.isBackground()) return false
        if (telephoneService.isCalling()) return false
        return try {
            val adId = AdFactory.findEcpmMaxAdIdForRewardVideoAd(rewardVideoIds)
            if (adId != null) {
                val act = ActivityStack.getTopActivity() ?: return false
                return AdFactory.loadRewardAdVideo(act, adId, weakReference?.get())
            }
            false
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    private fun loadInterstitialAd() {
        weakReference = null
        if (interstitialAdIds.isNullOrEmpty()) return
        if (ActivityStack.isBackground()) return
        if (telephoneService.isCalling()) return
        try {
            val adId = AdFactory.findEcpmMaxAdByRewardAndInters(rewardVideoIds, interstitialAdIds)
            if (adId != null) {
                val act = ActivityStack.getTopActivity() ?: return
                weakReference = WeakReference(object : AdAdaptPlayExternalListener() {
                    override fun onAdPlayStart() {
                        super.onAdPlayStart()
                        loadInterstitialAdVipTipView()
                    }
                })
                AdFactory.loadInterstitial(act, adId, weakReference?.get())
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun loadInterstitialAdVipTipView() {
        try {
            val activity = ActivityStack.getTopActivity() ?: return
            val parent = activity.window.decorView as ViewGroup
            val view = LayoutInflater.from(activity).inflate(R.layout.layout_interstitial_vip, null)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.BOTTOM
            params.bottomMargin = 20.dpToPx(activity)
            view.layoutParams = params
            parent.addView(view)
            view.setOnClickListener {
                RouteSdk.navigationActivity(RoutePage.STORE.VIP_STORE)
                UserBehavior.setChargeSource("vip_store")
            }
            parent.postDelayed({
                parent.removeView(view)
            }, 10 * 1000)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadSplashAd(activity: Activity, container: ViewGroup, goStepListener: (() -> Unit)?) {
        if (ActivityStack.isBackground()) {
            goStepListener?.invoke()
            return
        }
        AdFactory.loadSplashAd(
            activity,
            container,
            goStepListener = goStepListener,
            onAdShow = {})
    }
}