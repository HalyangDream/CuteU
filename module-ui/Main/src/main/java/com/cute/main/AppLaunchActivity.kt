package com.cute.main

import android.view.LayoutInflater
import com.cute.ad.AdFactory
import com.cute.baselogic.deviceDataStore
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.main.databinding.ActivityAppLaunchBinding
import com.cute.main.intent.LaunchIntent
import com.cute.main.state.LaunchState
import com.cute.tool.AppUtil
import com.cute.tool.JwtAuthUtil
import com.cute.uibase.ad.AdPlayService
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk

class AppLaunchActivity : BaseModelActivity<ActivityAppLaunchBinding, AppLaunchViewModel>() {

    override fun initViewBinding(layout: LayoutInflater): ActivityAppLaunchBinding {
        return ActivityAppLaunchBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        val splashIds = deviceDataStore.readSplashIds()
        if (!splashIds.isNullOrEmpty()) {
            AdFactory.cacheSplashAd(this@AppLaunchActivity, splashIds.toTypedArray(), false)
        }
        viewBinding.ivAppIcon.setImageDrawable(AppUtil.getAppIcon(this))
        viewBinding.tvAppName.text = AppUtil.getApplicationName(this)
        viewBinding.root.postDelayed({
            val curToken = userDataStore.readToken()
            if (curToken.isEmpty()) {
                val token = JwtAuthUtil.jwtGenerate(AppUtil.getAndroidID(this))
                viewModel.processIntent(LaunchIntent.LoginWithVisitor(token))
            } else {
                viewModel.processIntent(LaunchIntent.CheckToken)
            }

        }, 1000)

        viewModel.observerState {
            when (it) {
                is LaunchState.LoginSuccess -> {
                    userDataStore.saveToken(it.data.token)
                    userDataStore.saveUid(it.data.id)
                    loadSplashAd {
                        RouteSdk.navigationActivity(RoutePage.Main.MAIN_PAGE)
                        finish()
                    }

                }

                is LaunchState.UpdateTokenSuccess -> {
                    userDataStore.saveToken(it.token)
                    loadSplashAd {
                        RouteSdk.navigationActivity(RoutePage.Main.MAIN_PAGE)
                        finish()
                    }

                }

                is LaunchState.GoLogin -> {
                    userDataStore.clear()
                    loadSplashAd {
                        RouteSdk.navigationActivity(RoutePage.Login.LOGIN_PAGE)
                        finish()
                    }
                }
            }
        }
    }

    private fun loadSplashAd(startJump: () -> Unit) {
        if (deviceDataStore.readSplashIds().isNullOrEmpty()) {
            startJump()
            return
        }
        if (userDataStore.readVip()) {
            startJump()
            return
        }
        AdPlayService.loadSplashAd(this, viewBinding.root) {
            startJump()
        }
    }
}