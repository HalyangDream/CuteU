package com.amigo.main

import android.view.LayoutInflater
import com.amigo.ad.AdFactory
import com.amigo.baselogic.deviceDataStore
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.main.databinding.ActivityAppLaunchBinding
import com.amigo.main.intent.LaunchIntent
import com.amigo.main.state.LaunchState
import com.amigo.tool.AppUtil
import com.amigo.tool.JwtAuthUtil
import com.amigo.uibase.ad.AdPlayService
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk

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