package com.cute.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.analysis.Analysis
import com.cute.baselogic.statusDataStore
import com.cute.baselogic.storage.UserDataStore
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.im.IMCore
import com.cute.im.bean.Conversation
import com.cute.im.listener.ConversationListener
import com.cute.im.listener.IMStatusListener
import com.cute.im.service.MsgServiceObserver
import com.cute.logic.http.response.product.PackageShow
import com.cute.main.databinding.ActivityAppMainBinding
import com.cute.main.databinding.LayoutAppBottomBarBinding
import com.cute.main.dialog.LookRewardVideoAdCoinDialog
import com.cute.main.intent.AppMainIntent
import com.cute.main.state.AppMainState
import com.cute.picture.loadImage
import com.cute.tool.DragViewUtil
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.Toaster
import com.cute.uibase.adapter.ViewPagerAdapter
import com.cute.uibase.event.ChangeMainTabEvent
import com.cute.uibase.event.FollowerUnReadEvent
import com.cute.uibase.event.RefreshFeedVideoEvent
import com.cute.uibase.invisible
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.IStoreService
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = RoutePage.Main.MAIN_PAGE)
class AppMainActivity : BaseModelActivity<ActivityAppMainBinding, AppMainViewModel>(),
    ConversationListener, IMStatusListener {


    private lateinit var bottomBarBinding: LayoutAppBottomBarBinding
    private var lastExitAppTime: Long = 0


    override fun initViewBinding(layout: LayoutInflater): ActivityAppMainBinding {
        return ActivityAppMainBinding.inflate(layout)
    }

    override fun initView() {
        DragViewUtil.registerDragAction(viewBinding.ivProduct)
        val params = viewBinding.ivCoinFree.layoutParams as RelativeLayout.LayoutParams
        params.topMargin += StatusUtils.getStatusBarHeight(this)
        bottomBarBinding = LayoutAppBottomBarBinding.bind(viewBinding.root)
        viewModel.processIntent(AppMainIntent.InitService)
        viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
        val feedFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_FRAGMENT)
        val eventFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_EVENT_FRAGMENT)
        val matchFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_MATCH_FRAGMENT)
        val mineFragment = RouteSdk.getNavigationFragment(RoutePage.MINE.MINE_FRAGMENT)
        val videoFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_VIDEO_FRAGMENT)

        bottomBarBinding.ivMatch.tag = matchFragment
        bottomBarBinding.ivVideo.tag = videoFragment
        bottomBarBinding.ivHome.tag = feedFragment
        bottomBarBinding.ivMsg.tag = eventFragment
        bottomBarBinding.ivMe.tag = mineFragment

        val fragments = mutableListOf(
            feedFragment, videoFragment, matchFragment, eventFragment, mineFragment
        )
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragments)
        viewBinding.vp2.isUserInputEnabled = false
        viewBinding.vp2.isSaveEnabled = false
        viewBinding.vp2.offscreenPageLimit = fragments.size
        viewBinding.vp2.adapter = pagerAdapter
        changeTab(0)
        addViewListener()
        viewModel.observerState {
            when (it) {
                is AppMainState.UpdatePersonInfo -> {
                    userDataStore.saveUid(it.info.id)
                    userDataStore.saveVip(it.info.isVip)
                    userDataStore.saveRole(it.info.role)
                    userDataStore.saveAvatar(it.info.avatar)
                    userDataStore.saveCoinMode(it.info.isCoinMode)
                    Analysis.loginAccount("${it.info.id}")
                }

                is AppMainState.UnReadCount -> updateUnReadCount(it.count)

                is AppMainState.ProductHomeInfo -> bindProductHomeUI(it.info)
            }
        }
        viewBinding.ivProduct.setOnClickListener {
            val popCode = it.tag
            if (popCode != null && popCode is String) {
                RouteSdk.findService(IStoreService::class.java).showCodeDialog(popCode, null)
                UserBehavior.setChargeSource("special_offers")
            }
        }

        viewBinding.ivCoinFree.setOnClickListener {
            val dialog = LookRewardVideoAdCoinDialog()
            dialog.showDialog(it.context, null)
        }


        IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, true)
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, true)

        EventBus.event.subscribe<ChangeMainTabEvent>(lifecycleScope) {
            changeTab(it.position)
        }
        EventBus.event.subscribe<FollowerUnReadEvent>(lifecycleScope) {
            viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val curTime = System.currentTimeMillis()
                if (curTime - lastExitAppTime <= 2000) {
                    moveTaskToBack(true)
                } else {
                    lastExitAppTime = curTime
                    Toaster.showShort(
                        this@AppMainActivity, com.cute.uibase.R.string.str_exit_app_tip
                    )
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.startForegroundService(this, Intent(this, CoreService::class.java))
    }

    override fun onResume() {
        super.onResume()
        viewModel.processIntent(AppMainIntent.NewUserProductInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, false)
        IMCore.getService(MsgServiceObserver::class.java).observerIMStatus(this, false)
    }


    override fun loginSuccess() {
        viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
    }

    override fun renewToken() {
    }

    override fun kickOut() {
        lifecycleScope.launch(Dispatchers.Main) {
            navigationLogin()
        }
    }

    override fun reLogin() {
        viewModel.processIntent(AppMainIntent.InitService)
        viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
    }

    override fun onServerBanned() {
        lifecycleScope.launch(Dispatchers.Main) {
            navigationLogin()
        }
    }

    override fun onConversationChange(conversation: Conversation) {
        viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
    }

    override fun onConversationDelete(list: MutableList<Conversation>) {
        viewModel.processIntent(AppMainIntent.GetUnReadCount(userDataStore.getUid()))
    }

    private fun addViewListener() {
        bottomBarBinding.ivHome.setOnClickListener {
            val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
            val index = adapter.getIndexOfFragment(it.tag as Fragment)
            changeTab(index)
        }

        bottomBarBinding.ivVideo.setOnClickListener {
            val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
            val index = adapter.getIndexOfFragment(it.tag as Fragment)
            val curIndex = viewBinding.vp2.currentItem
            if (index != curIndex) {
                EventBus.post(RefreshFeedVideoEvent("tab_video"))
            }
            changeTab(index)
        }

        bottomBarBinding.ivMatch.setOnClickListener {
            val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
            val index = adapter.getIndexOfFragment(it.tag as Fragment)
            changeTab(index)
        }

        bottomBarBinding.ivMsg.setOnClickListener {
            val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
            val index = adapter.getIndexOfFragment(it.tag as Fragment)
            changeTab(index)
        }

        bottomBarBinding.ivMe.setOnClickListener {
            val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
            val index = adapter.getIndexOfFragment(it.tag as Fragment)
            changeTab(index)
        }
    }

    private fun changeTab(index: Int) {
        viewBinding.vp2.setCurrentItem(index, false)
        val adapter = viewBinding.vp2.adapter as ViewPagerAdapter
        val fragment = adapter.getFragmentOfIndex(index) ?: return
        for (i in 0..bottomBarBinding.clBottom.childCount) {
            val view = bottomBarBinding.clBottom.getChildAt(i)
            if (view !is ImageView) continue
            if (view.visibility != View.VISIBLE) continue
            view.isSelected = view.tag == fragment
        }
    }

    private fun updateUnReadCount(count: Int) {
        bottomBarBinding.tvUnreadCount.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
        if (count > 0) {
            val unReadNum = if (count > 99) "99+" else "$count"
            bottomBarBinding.tvUnreadCount.text = unReadNum
            bottomBarBinding.svUnreadFollow.invisible()
        } else {
            val followUnReadCount = UserDataStore.get(this).getLikeMeUnReadCount()
            bottomBarBinding.svUnreadFollow.visibility =
                if (followUnReadCount > 0) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun navigationLogin() {
        userDataStore.clear()
        Toaster.showShort(this, com.cute.uibase.R.string.str_account_expired)
        RouteSdk.navigationLoginActivity()
    }

    private fun bindProductHomeUI(info: PackageShow?) {
        if (info != null) {
            viewBinding.ivProduct.visible()
            viewBinding.ivProduct.loadImage(
                info.cover, com.cute.uibase.R.drawable.img_placehoder
            )
            viewBinding.ivProduct.tag = "${info.popCode}"
        } else {
            viewBinding.ivProduct.invisible()
            viewBinding.ivProduct.tag = null
        }
    }


}