package com.amigo.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.collection.arraySetOf
import com.amigo.ad.AdFactory
import com.amigo.analysis.Analysis
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.amigo.app.BuildConfig
import com.amigo.baselogic.deviceDataStore
import com.amigo.baselogic.storage.DeviceDataStore
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.chat.view.CustomChattingView
import com.amigo.chat.view.im.CallRecordLeftView
import com.amigo.chat.view.im.CallRecordRightView
import com.amigo.chat.view.im.ImageLeftView
import com.amigo.chat.view.im.ImageRightView
import com.amigo.chat.view.im.TextLeftView
import com.amigo.chat.view.im.TextRightView
import com.amigo.chat.view.im.UnknownView
import com.amigo.chat.view.im.VideoLeftView
import com.amigo.chat.view.im.VideoRightView
import com.amigo.http.ApiClient
import com.amigo.http.ApiConfig
import com.amigo.http.ApiResponse
import com.amigo.http.HandleApiResponseListener
import com.amigo.im.IMCore
import com.amigo.logic.http.HttpCommonParam
import com.amigo.message.custom.msg.ImageMessage
import com.amigo.message.custom.msg.TextMessage
import com.amigo.message.custom.msg.VideoMessage
import com.amigo.picture.ImageLoaderWrapper
import com.amigo.tool.AppUtil
import com.amigo.basic.language.MultiLanguages
import com.amigo.chat.view.im.BlurImageLeftView
import com.amigo.chat.view.im.BlurVideoLeftView
import com.amigo.message.custom.msg.BlurImageMessage
import com.amigo.message.custom.msg.BlurVideoMessage
import com.amigo.message.custom.msg.CallRecordMessage
import com.amigo.message.custom.notify.PaySuccessNotify
import com.amigo.message.custom.notify.PopCodeNotify
import com.amigo.message.custom.notify.RatingDialogNotify
import com.amigo.message.custom.notify.StrategyCallNotify
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.Constant
import com.amigo.uibase.RefreshFooterView
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.media.VideoPlayerManager
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
            header.setColorSchemeResources(com.amigo.uibase.R.color.app_main_color)
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
                paramObject["firebase_id"] =
                    DeviceDataStore.get(this@SocialApp).getFirebaseId()
                paramObject["third_party_id"] =
                    DeviceDataStore.get(this@SocialApp).getThirdPartyId()
//                paramObject["adjust_id"] = DeviceDataStore.get(this@SocialApp).getAdJustId()
                paramObject["token"] = UserDataStore.get(this@SocialApp).readToken()
                paramObject["locale"] = MultiLanguages.getCAppLanguage()
                paramObject["sys_language"] = AppUtil.getSysLocale().language
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
        Analysis.getFirebaseId(this){
            deviceDataStore.saveFirebaseId(it)
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
            requestBuilder.addHeader(
                "Firebase_Id", DeviceDataStore.get(this@SocialApp).getFirebaseId()
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