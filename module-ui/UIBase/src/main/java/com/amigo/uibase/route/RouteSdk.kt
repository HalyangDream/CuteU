package com.amigo.uibase.route

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.BuildConfig
import com.alibaba.android.arouter.launcher.ARouter
import com.amigo.uibase.route.provider.IStoreService

object RouteSdk {

    fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(application)
    }


    fun navigationLoginActivity() {
        ARouter.getInstance().build(RoutePage.Login.LOGIN_PAGE)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()
    }

    fun navigationActivity(url: String) {
        ARouter.getInstance().build(url)
            .navigation()
    }

    fun navigationActivity(url: String, flags: Int) {
        ARouter.getInstance().build(url)
            .addFlags(flags)
            .navigation()
    }

    fun navigationActivity(url: String, bundle: Bundle) {
        ARouter.getInstance().build(url)
            .with(bundle)
            .navigation()
    }

    fun navigationUserDetail(anchorId: Long, source: String) {
        ARouter.getInstance().build(RoutePage.HOME.ANCHOR_DETAIL_ACTIVITY)
            .withLong("anchorId", anchorId)
            .withString("source", source)
            .navigation()
    }

    fun navigationChat(peerId: Long, source: String) {
        ARouter.getInstance().build(RoutePage.CHAT.CHAT_ACTIVITY)
            .withLong("peerId", peerId)
            .withString("source", source)
            .navigation()
    }

    fun navigationVipStore() {
        ARouter.getInstance().build(RoutePage.STORE.VIP_STORE)
            .navigation()
    }


    fun getNavigationFragment(url: String): Fragment {
        return ARouter.getInstance().build(url).navigation() as Fragment
    }

    fun <T> findService(clazz: Class<T>): T {
        return ARouter.getInstance().navigation(clazz)
    }

    fun handleResponseCode(code: String, dialogBundle: Bundle?): Boolean {
        val iStoreService = findService(IStoreService::class.java)
        return iStoreService.showCodeDialog(code, dialogBundle)
    }

}