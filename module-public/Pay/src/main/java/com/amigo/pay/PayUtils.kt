package com.amigo.pay

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

/**
 * author : mac
 * date   : 2022/4/20
 *
 */
internal object PayUtils {


    /**
     * 获取PayTm的版本号
     *
     * @param context
     * @return
     */
    fun getPaytmVersion(context: Context): String? {
        val packages: MutableList<String> = ArrayList()
        try {
            val packageInfos = context.packageManager.getInstalledPackages(
                PackageManager.GET_ACTIVITIES or
                        PackageManager.GET_SERVICES
            )
            for (info in packageInfos) {
                val pkg = info.packageName
                packages.add(pkg)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        val pm = context.packageManager
        try {
            val pkgInfo: PackageInfo =
                pm.getPackageInfo("net.one97.paytm", PackageManager.GET_ACTIVITIES)
            return pkgInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            // Paytm app not installed
            e.printStackTrace()
        }
        return null
    }

    /**
     * 比较版本号
     *
     * @param str1
     * @param str2
     * @return
     */
    fun compareVersion(str1: String?, str2: String?): Int {
        if (str1.isNullOrEmpty() || str2.isNullOrEmpty()) {
            return 1
        }
        val vals1 = str1.split(".").toTypedArray()
        val vals2 = str2.split(".").toTypedArray()
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
     * 判断 paytm的版本决定用哪一种方式支付
     * 大于8.6.0使用高版本，小于8.6.0使用低版本
     * @return true使用goHigher860Paytm，false使用goLess860Paytm
     */
    fun paytmApp(paytmVersion: String?): Boolean {
        return compareVersion(paytmVersion, "8.6.0") >= 0
    }


    const val PAYTM_PAT_REQUEST_CODE = 9999

    /**
     * 打开paytm 低版本
     *
     * @param context
     * @param activity
     * @param bundle
     */
    fun goLess860Paytm(
        activity: Activity,
        amount: String?,
        orderId: String?,
        txnToken: String?,
        mid: String?
    ): Boolean {
        if (amount.isNullOrEmpty()
            || orderId.isNullOrEmpty()
            || txnToken.isNullOrEmpty()
            || mid.isNullOrEmpty()
        ) {
            return false
        }
        val paytmIntent = Intent()
        val bundle = Bundle()
        bundle.putDouble("nativeSdkForMerchantAmount", amount.toDouble())
        bundle.putString("orderid", orderId)
        bundle.putString("txnToken", txnToken)
        bundle.putString("mid", mid)
        paytmIntent.component = ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash")
        // You must have to pass hard coded 2 here, Else your transaction would not proceed.
        paytmIntent.putExtra("paymentmode", 2)
        paytmIntent.putExtra("bill", bundle)
        activity.startActivityForResult(paytmIntent, PAYTM_PAT_REQUEST_CODE)
        return true
    }

    /**
     * 打开paytm 高版本
     *
     * @param context
     * @param activity
     * @param bundle
     */
    fun goHigher860Paytm(
        activity: Activity,
        amount: String?,
        orderId: String?,
        txnToken: String?,
        mid: String?
    ): Boolean {
        if (amount.isNullOrEmpty()
            || orderId.isNullOrEmpty()
            || txnToken.isNullOrEmpty()
            || mid.isNullOrEmpty()
        ) {
            return false
        }
        val paytmIntent = Intent()
        paytmIntent.component =
            ComponentName("net.one97.paytm", "net.one97.paytm.AJRRechargePaymentActivity")
        paytmIntent.putExtra("paymentmode", 2)
        paytmIntent.putExtra("enable_paytm_invoke", true)
        paytmIntent.putExtra("paytm_invoke", true)
        //this is string amount
        paytmIntent.putExtra("price", amount)
        paytmIntent.putExtra("nativeSdkEnabled", true)
        paytmIntent.putExtra("orderid", orderId)
        paytmIntent.putExtra("txnToken", txnToken)
        paytmIntent.putExtra("mid", mid)
        activity.startActivityForResult(paytmIntent, PAYTM_PAT_REQUEST_CODE)
        return true
    }

    const val UPI_PAT_REQUEST_CODE = 8888

    /**
     * 打开手机上支持UPI支付的应用
     *
     * @param activity  上下文
     * @param upiPayUrl upi的支付链接
     * @return true 打开成功，false 打开失败
     */
    fun launchUPIPay(activity: Activity, upiPayUrl: String?): Boolean {
        try {
            if (upiPayUrl.isNullOrEmpty()) {
                return false
            }
//            val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"))
//            if (upiIntent.resolveActivity(activity.packageManager) == null) {
//                return false
//            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiPayUrl))
            intent.data = Uri.parse(upiPayUrl)
            val chooser = Intent.createChooser(intent, "Pay with")
            activity.startActivityForResult(chooser, UPI_PAT_REQUEST_CODE)
            return true
        } catch (ex: Exception) {
            return false
        }
    }

}