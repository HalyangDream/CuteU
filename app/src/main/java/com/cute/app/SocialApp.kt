package com.cute.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.collection.arraySetOf
import com.cute.ad.AdFactory
import com.cute.analysis.Analysis
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.cute.app.BuildConfig
import com.cute.baselogic.deviceDataStore
import com.cute.baselogic.storage.DeviceDataStore
import com.cute.baselogic.storage.UserDataStore
import com.cute.chat.view.CustomChattingView
import com.cute.chat.view.im.CallRecordLeftView
import com.cute.chat.view.im.CallRecordRightView
import com.cute.chat.view.im.ImageLeftView
import com.cute.chat.view.im.ImageRightView
import com.cute.chat.view.im.TextLeftView
import com.cute.chat.view.im.TextRightView
import com.cute.chat.view.im.UnknownView
import com.cute.chat.view.im.VideoLeftView
import com.cute.chat.view.im.VideoRightView
import com.cute.http.ApiClient
import com.cute.http.ApiConfig
import com.cute.http.ApiResponse
import com.cute.http.HandleApiResponseListener
import com.cute.im.IMCore
import com.cute.logic.http.HttpCommonParam
import com.cute.message.custom.msg.ImageMessage
import com.cute.message.custom.msg.TextMessage
import com.cute.message.custom.msg.VideoMessage
import com.cute.picture.ImageLoaderWrapper
import com.cute.tool.AppUtil
import com.cute.basic.language.MultiLanguages
import com.cute.chat.view.im.BlurImageLeftView
import com.cute.chat.view.im.BlurVideoLeftView
import com.cute.message.custom.msg.BlurImageMessage
import com.cute.message.custom.msg.BlurVideoMessage
import com.cute.message.custom.msg.CallRecordMessage
import com.cute.message.custom.notify.PaySuccessNotify
import com.cute.message.custom.notify.PopCodeNotify
import com.cute.message.custom.notify.RatingDialogNotify
import com.cute.message.custom.notify.StrategyCallNotify
import com.cute.uibase.ActivityStack
import com.cute.uibase.Constant
import com.cute.uibase.RefreshFooterView
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.media.VideoPlayerManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder
import java.nio.charset.Charset


class SocialApp : Application() {



    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            val header = MaterialHeader(context)
            header.setColorSchemeResources(com.cute.uibase.R.color.app_main_color)
            header
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            RefreshFooterView(context)
        }
    }


    override fun onCreate() {
        super.onCreate()
        initSDK()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(MultiLanguages.attach(base))
    }


    private fun initSDK() {
        RouteSdk.init(this)
        MultiLanguages.init(this)
        initIM()
        initHttp()
        VideoPlayerManager.init(this)
        Constant.PRIVACY_AGREEMENT = BuildConfig.PRIVACY_AGREEMENT
        Constant.USER_AGREEMENT = BuildConfig.USER_AGREEMENT
        ActivityStack.application = this
        registerActivityLifecycleCallbacks(ActivityStack)
        Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            ImageLoaderWrapper.initialize(this)
            initAnalysis()
            initAd()
//            initAttribution()

        }.start()
    }

    private fun initHttp() {
        ApiClient.initializationApi(
            isDebugMode = BuildConfig.DEBUG,
            config = ApiConfig.Builder().setBaseUrl(BuildConfig.APP_URL)
                .setInterceptor(HeaderInterceptor(), S3Interceptor()).setReadTimeout(30)
                .setConnectTimeout(10).setWriteTimeout(10)
                .setHandleResponseListener(object : HandleApiResponseListener {
                    override fun onHandle(response: ApiResponse<*>, dialogBundle: Bundle?) {
                        if (!response.code.isNullOrEmpty()) {
                            RouteSdk.handleResponseCode(response.code, dialogBundle)
                        }
                    }
                }).build()
        )
        HttpCommonParam.setHttpCommonParamConfig(object : HttpCommonParam.HttpCommonParamConfig {
            override fun getParam(): Map<String, Any> {
                val paramObject = mutableMapOf<String, Any>()
                paramObject["os"] = "Android"
                paramObject["osvers"] = AppUtil.getOSVersion()
                paramObject["make"] = AppUtil.getOSBrand()
                paramObject["model"] = AppUtil.getOSModel()
                paramObject["locale"] = AppUtil.getSysLocale().language
                paramObject["network"] = AppUtil.getNetwork(this@SocialApp)
                paramObject["version"] = AppUtil.getAppVersion(this@SocialApp)
                paramObject["device_id"] = AppUtil.getAndroidID(this@SocialApp)
                paramObject["mcc"] = AppUtil.getMCC(this@SocialApp)
                paramObject["has_sim"] = AppUtil.hasSimCard(this@SocialApp)
                paramObject["is_dev_mod"] = AppUtil.isDevMode(this@SocialApp)
                paramObject["app_count"] = AppUtil.getUserAppCount(this@SocialApp)
                paramObject["carrier"] = AppUtil.getCarrier(this@SocialApp)
                paramObject["utm"] = DeviceDataStore.get(this@SocialApp).getReferrer()
                paramObject["ad_id"] = DeviceDataStore.get(this@SocialApp).getAdId()
                paramObject["sim_country"] = AppUtil.simCountry(this@SocialApp)
                paramObject["third_party_id"] =
                    DeviceDataStore.get(this@SocialApp).getThirdPartyId()
//                paramObject["adjust_id"] = DeviceDataStore.get(this@SocialApp).getAdJustId()
                paramObject["token"] = UserDataStore.get(this@SocialApp).readToken()
                paramObject["locale"] = MultiLanguages.getCAppLanguage()
                paramObject["app_id"] = BuildConfig.APP_ID
                val uid = UserDataStore.get(this@SocialApp).getUid()
                if (uid != 0L) {
                    paramObject["uid"] = uid.toInt()
                }
                return paramObject
            }
        })
    }

    private fun initIM() {
        IMCore.initDbAndListener(this)
        IMCore.registerNotifyType(
            PaySuccessNotify(), StrategyCallNotify(), RatingDialogNotify(), PopCodeNotify()
        )
        IMCore.registerMessageType(
            TextMessage(),
            ImageMessage(),
            VideoMessage(),
            BlurImageMessage(),
            BlurVideoMessage(),
            CallRecordMessage()
        )

        CustomChattingView.registerViewType(
            TextLeftView::class,
            TextRightView::class,
            ImageLeftView::class,
            ImageRightView::class,
            VideoLeftView::class,
            VideoRightView::class,
            BlurImageLeftView::class,
            BlurVideoLeftView::class,
            CallRecordLeftView::class,
            CallRecordRightView::class,
            UnknownView::class
        )
    }

    private fun initAnalysis() {
        Analysis.initAnalysis(
            this,
            BuildConfig.APP_ID,
            BuildConfig.DT_APP_ID,
            BuildConfig.DT_SERVER_URL,
            BuildConfig.DEBUG
        )
        deviceDataStore.saveAdId(Analysis.getGoogleAdId(this) ?: "")
        Analysis.getDtId {
            deviceDataStore.saveThirdPartyId(it)
        }
    }

    private fun initAd() {
        AdFactory.createFactory(
            this, BuildConfig.DEBUG, BuildConfig.TOP_ON_ID, BuildConfig.TOP_ON_KEY
        )
    }

    private fun initAttribution() {
//        val environment: String =
//            if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
//        val adJustConfig = AdjustConfig(this, BuildConfig.ATTRIBUTION_KEY, environment)
//        if (BuildConfig.DEBUG) adJustConfig.setLogLevel(LogLevel.VERBOSE)
//        else adJustConfig.setLogLevel(LogLevel.SUPRESS)
//        adJustConfig.urlStrategy = AdjustConfig.URL_STRATEGY_INDIA
//        adJustConfig.setOnAttributionChangedListener {
//            val referrer = if (it.network.isNullOrEmpty()) "Organic" else it.network
//            DeviceDataStore.get(this).saveReferrer(referrer)
//            DeviceDataStore.get(this).saveAdJustId(it.adid)
//        }
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//
//            }
//
//            override fun onActivityStarted(activity: Activity) {
//            }
//
//            override fun onActivityResumed(activity: Activity) {
//                Adjust.onResume()
//            }
//
//            override fun onActivityPaused(activity: Activity) {
//                Adjust.onPause()
//            }
//
//            override fun onActivityStopped(activity: Activity) {
//            }
//
//            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//            }
//
//            override fun onActivityDestroyed(activity: Activity) {
//            }
//        })
//        Adjust.onCreate(adJustConfig)
    }


    private inner class HeaderInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder: Request.Builder = request.newBuilder()
            requestBuilder.addHeader("OS", "Android")
            requestBuilder.addHeader("App_Id", "${BuildConfig.APP_ID}")
            requestBuilder.addHeader("OS_Version", AppUtil.getOSVersion())
            requestBuilder.addHeader("Device_Brand", AppUtil.getOSBrand())
            requestBuilder.addHeader("Device_Model", AppUtil.getOSModel())
            requestBuilder.addHeader("App_Version", AppUtil.getAppVersion(this@SocialApp))
            requestBuilder.addHeader("App_Bundle", AppUtil.getPackageName(this@SocialApp))
            requestBuilder.addHeader("Device_Id", AppUtil.getAndroidID(this@SocialApp))
            requestBuilder.addHeader("Locale", AppUtil.getAppVersion(this@SocialApp))
            requestBuilder.addHeader("Language", AppUtil.getSysLanguage())
            requestBuilder.addHeader("Network", AppUtil.getNetwork(this@SocialApp))
            requestBuilder.addHeader(
                "Carrier", URLEncoder.encode(
                    AppUtil.getCarrier(this@SocialApp)
                )
            )
            requestBuilder.addHeader("Mcc", "${AppUtil.getMCC(this@SocialApp)}")
            requestBuilder.addHeader("Dev_Mod", "${AppUtil.isDevMode(this@SocialApp)}")
            requestBuilder.addHeader("Has_Sim", "${AppUtil.hasSimCard(this@SocialApp)}")
            requestBuilder.addHeader("SIm_Country", AppUtil.simCountry(this@SocialApp))
            requestBuilder.addHeader("Authorization", UserDataStore.get(this@SocialApp).readToken())
            requestBuilder.addHeader(
                "Third_Party_Id", DeviceDataStore.get(this@SocialApp).getThirdPartyId()
            )
            return chain.proceed(requestBuilder.build())
        }
    }

    private inner class S3Interceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (request.url.host.contains("amazonaws.com")) {
                val requestBuilder: Request.Builder = request.newBuilder()
                val headers = arraySetOf<String>()
                for (name in request.headers.names()) {
                    if (!isNeed(name)) {
                        headers.add(name)
                    }
                }
                for (header in headers) {
                    requestBuilder.removeHeader(header)
                }
                return chain.proceed(requestBuilder.build())
            }
            return chain.proceed(request)
        }

        private fun isNeed(name: String): Boolean {
            return name.contains("host", true) or name.contains(
                "X-Amz", true
            ) or name.contains("content-type", true) or name.contains("content-length", true)
        }
    }

}