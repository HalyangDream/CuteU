package com.amigo.tool

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.content.ContextCompat.getSystemService
import java.text.DecimalFormat
import java.util.Locale


object AppUtil {


    /**
     * 获取网络MCC
     *
     * @return
     */
    fun getMCC(context: Context): Int {
        return try {
            val telManager = context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val mcc = telManager.simOperator
            if (!TextUtils.isEmpty(mcc) && mcc.length > 3) {
                mcc.substring(0, 3).toInt()
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获取Carrier
     *
     * @return
     */
    fun getCarrier(context: Context): String {
        return try {
            val telManager = context.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val operatorName = telManager.simOperatorName
            if (!TextUtils.isEmpty(operatorName)) {
                operatorName
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取网络信息
     *
     * @return
     */
    fun getNetwork(context: Context): String {
        // 获取网络服务 1:wifi 0:mobile
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        return if (networkInfo != null && networkInfo.isAvailable) {
            if (networkInfo.type == 0) "mobile" else if (networkInfo.type == 1) "wifi" else "unknown"
        } else "unknown"
    }

    /**
     * 判断是否有SIM卡
     *
     * @param context
     * @return
     */
    fun hasSimCard(context: Context): Boolean {
        val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simState = telMgr.simState
        var result = true
        when (simState) {
            TelephonyManager.SIM_STATE_ABSENT, TelephonyManager.SIM_STATE_UNKNOWN -> result =
                false // 没有SIM卡
        }
        return result
    }

    fun simCountry(context: Context): String {
        if (hasSimCard(context)) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val countryCode = telephonyManager?.simCountryIso
            return countryCode ?: ""
        }
        return ""
    }

    fun getAndroidID(context: Context): String {
        val id = Settings.Secure.getString(
            context.getContentResolver(), Settings.Secure.ANDROID_ID
        )
        return id ?: ""
    }

    /**
     * 是否是开发者模式
     *
     * @return
     */
    fun isDevMode(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.getContentResolver(), Settings.Global.ADB_ENABLED, 0
        ) > 0
    }

    /**
     * 获取用户安装的应用数量
     *
     * @return
     */
    private var userAppCount = 0
    fun getUserAppCount(context: Context): Int {
        if (userAppCount > 0) return userAppCount
        try {
            userAppCount = 0
            val applicationInfos: List<ApplicationInfo> =
                context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            for (info in applicationInfos) {
                // 非系统应用
                if (info.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    userAppCount += 1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return userAppCount
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    fun getOSBrand(): String {
        return Build.BRAND
    }

    /**
     * 获取当前系统版本
     *
     * @return
     */
    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    /***
     * 获取设备型号
     * @return
     */
    fun getOSModel(): String {
        return Build.MODEL
    }

    /**
     * 获取包名
     *
     * @return
     */
    fun getPackageName(context: Context): String {
        return context.getPackageName()
    }

    /**
     * 获取当前应用版本号
     */
    fun getAppVersion(context: Context): String {
        return try {
            val packageManager: PackageManager = context.getPackageManager()
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(context.getPackageName(), 0)
            packageInfo.versionName!!
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 获取当前应用版本号
     */
    fun getAppVersionCode(context: Context): Int {
        return try {
            val packageManager: PackageManager = context.getPackageManager()
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(context.getPackageName(), 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }

    // 通过包名获取对应的 Drawable 数据
    fun getAppIcon(context: Context): Drawable? {
        try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(getPackageName(context))
            return intent?.let { pm.getActivityIcon(it.component!!) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取系统语言
     *
     * @return
     */
    fun getSysLanguage(): String {
        val locale: Locale
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        return locale.language
    }

    /**
     * 获取系统语言
     *
     * @return
     */
    fun getSysLocale(): Locale {

        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
        return locale
    }

    /**
     * 获取App语言
     *
     * @return
     */
    fun getAppLanguage(): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        return locale.language
    }

    /**
     * 获取系统语言
     *
     * @return
     */
    fun getAppLocale(): Locale {

        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        return locale
    }

    /**
     * 获取当前渠道名
     *
     * @param var0
     * @return
     */
    private var channelName: String? = null

    fun getChannelByXML(var0: Context): String? {
        try {
            val var2 = var0.packageManager
            val var3 = var2.getApplicationInfo(var0.packageName, PackageManager.GET_META_DATA)
            if (var3 != null && var3.metaData != null) {
                val var4 = var3.metaData["UMENG_CHANNEL"]
                if (var4 != null) {
                    val var5 = var4.toString()
                    if (var5 != null) {
                        channelName = var5.trim { it <= ' ' }
                        return channelName
                    }
                }
            }
        } catch (var6: Throwable) {
            channelName = "organic"
        }
        return channelName
    }

    /**
     * 获取MetaData
     *
     * @param defaultValue
     * @return
     */
    fun getMetaData(context: Context, key: String?, defaultValue: String): String? {
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            return appInfo.metaData.getString(key)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return defaultValue
    }

    fun getApplicationName(context: Context): String {
        try {
            val packageManager: PackageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(
                getPackageName(context)!!, 0
            )
            return packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 比较版本号
     *
     * @param str1
     * @param str2
     * @return
     */
    fun compareVersion(str1: String, str2: String): Int {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            return 1
        }
        val vals1 = str1.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val vals2 = str2.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i = 0
        //set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.size && i < vals2.size && vals1[i].equals(vals2[i], ignoreCase = true)) {
            i++
        }
        //compare first non-equal ordinal number
        if (i < vals1.size && i < vals2.size) {
            val diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]))
            return Integer.signum(diff)
        }
        //e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.size - vals2.size)
    }


    /**
     * 大小单位  k
     *
     * @param k
     * @return
     */
    fun toKString(fileSize: Long): String {
        val df = DecimalFormat("0.00")
        val fileSizeString: String
        fileSizeString = if (fileSize < 1000) {
            fileSize.toInt().toString() + ""
        } else {
            df.format(fileSize.toDouble() / 1000) + "k"
        }
        return fileSizeString
    }

    /**
     * 判断服务是否正在运行
     *
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true在运行 false 不在运行
     */
    fun isServiceRunning(context: Context, className: String): Boolean {
        var isRunning = false
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //获取所有的服务
        val services = activityManager.getRunningServices(Int.MAX_VALUE)
        if (services != null && services.size > 0) {
            for (service in services) {
                if (className == service.service.className) {
                    isRunning = true
                    break
                }
            }
        }
        return isRunning
    }


    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        return try {
            val packageManager = context.packageManager
            // 通过包名获取应用信息
            packageManager.getPackageInfo(packageName!!, PackageManager.GET_ACTIVITIES)
            true // 找到应用，已安装
        } catch (e: PackageManager.NameNotFoundException) {
            false // 未找到应用，未安装
        } catch (e: Exception) {
            false
        }
    }
}