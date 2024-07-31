package com.cute.ad

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.FrameLayout
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.ATShowConfig
import com.anythink.core.api.AdError
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeEventListener
import com.anythink.nativead.api.ATNativeNetworkListener
import com.anythink.nativead.api.NativeAd

class NativeAdView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mNative: ATNative? = null
    private var mNativeAd: NativeAd? = null

    init {
        val atNativeView = ATNativeAdView(context)
        addView(
            atNativeView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
    }

    fun loadNativeAd(placementId: String, loaderResult: ((result: Boolean) -> Unit)? = null) {
        this.isClickable = false
        requestAd(placementId) {
            mNativeAd = mNative?.getNativeAd(ATShowConfig.Builder().build())
            mNativeAd?.setNativeEventListener(object : ATNativeEventListener {
                override fun onAdImpressed(p0: ATNativeAdView?, atAdInfo: ATAdInfo?) {
                }

                override fun onAdClicked(p0: ATNativeAdView?, atAdInfo: ATAdInfo?) {
                }

                override fun onAdVideoStart(p0: ATNativeAdView?) {
                }

                override fun onAdVideoEnd(p0: ATNativeAdView?) {
                }

                override fun onAdVideoProgress(p0: ATNativeAdView?, p1: Int) {
                }
            })
            try {
                val view = getChildAt(0) as ATNativeAdView
                ATNative.entryAdScenario(placementId, "")
                mNativeAd?.renderAdContainer(view, null)
                mNativeAd?.prepare(view, null)
                requestAd(placementId){}
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun requestAd(placementId: String, listener: (() -> Unit)?) {
        mNative = ATNative(context, placementId, object : ATNativeNetworkListener {
            override fun onNativeAdLoaded() {
                Log.i("NativeAdView", "cacheNativeAd onNativeAdLoaded")
                listener?.invoke()
            }

            override fun onNativeAdLoadFail(p0: AdError?) {
                Log.i("NativeAdView", "cacheNativeAd onNativeAdLoadFail:${p0?.fullErrorInfo}")
            }
        })
        val localMap: HashMap<String, Any> = hashMapOf()
        localMap[ATAdConst.KEY.AD_WIDTH] = dpToPx(292) //单位：px，期望展示广告的宽度
        localMap[ATAdConst.KEY.AD_HEIGHT] = dpToPx(45)
        mNative?.setLocalExtra(localMap)
        mNative?.makeAdRequest()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mNativeAd?.destory()
        mNativeAd?.setNativeEventListener(null)
        mNative = null
        mNativeAd = null
        removeAllViews()
    }


    private fun dpToPx(dpValue: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), context.resources.displayMetrics
        ).toInt()
    }

    private fun dpToPx(dpValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics
        )
    }
}