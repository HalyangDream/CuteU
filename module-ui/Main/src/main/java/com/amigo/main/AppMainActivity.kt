package com.amigo.main

import android.content.Intent
import android.graphics.Color
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
import com.amigo.analysis.Analysis
import com.amigo.baselogic.statusDataStore
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.im.IMCore
import com.amigo.im.bean.Conversation
import com.amigo.im.listener.ConversationListener
import com.amigo.im.listener.IMStatusListener
import com.amigo.im.service.MsgServiceObserver
import com.amigo.logic.http.response.product.PackageShow
import com.amigo.main.databinding.ActivityAppMainBinding
import com.amigo.main.databinding.LayoutAppBottomBarBinding
import com.amigo.main.dialog.LookRewardVideoAdCoinDialog
import com.amigo.main.intent.AppMainIntent
import com.amigo.main.state.AppMainState
import com.amigo.picture.loadImage
import com.amigo.tool.DragViewUtil
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.tool.Toaster
import com.amigo.uibase.adapter.ViewPagerAdapter
import com.amigo.uibase.event.ChangeMainTabEvent
import com.amigo.uibase.event.FollowerUnReadEvent
import com.amigo.uibase.event.RefreshFeedVideoEvent
import com.amigo.uibase.invisible
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.visible
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
//        val matchFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_MATCH_FRAGMENT)
        val mineFragment = RouteSdk.getNavigationFragment(RoutePage.MINE.MINE_FRAGMENT)
        val videoFragment = RouteSdk.getNavigationFragment(RoutePage.HOME.FEED_VIDEO_FRAGMENT)

//        bottomBarBinding.ivMatch.tag = matchFragment
        bottomBarBinding.ivVideo.tag = videoFragment
        bottomBarBinding.ivHome.tag = feedFragment
        bottomBarBinding.ivMsg.tag = eventFragment
        bottomBarBinding.ivMe.tag = mineFragment

        val fragments = mutableListOf(
            feedFragment, videoFragment, eventFragment, mineFragment
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
                        this@AppMainActivity, com.amigo.uibase.R.string.str_exit_app_tip
                    )
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        CoreService.getInstance().onCreate(this)
//        ContextCompat.startForegroundService(this, Intent(this, CoreService::class.java))
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
        viewBinding.vp2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    bottomBarBinding.clBottom.setBackgroundColor(Color.parseColor("#80000000"))
                } else {
                    bottomBarBinding.clBottom.setBackgroundColor(
                        ContextCompat.getColor(
                            this@AppMainActivity,
                            android.R.color.white
                        )
                    )
                }
            }
        })

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
        Toaster.showShort(this, com.amigo.uibase.R.string.str_account_expired)
        RouteSdk.navigationLoginActivity()
    }

    private fun bindProductHomeUI(info: PackageShow?) {
        if (info != null) {
            viewBinding.ivProduct.visible()
            viewBinding.ivProduct.loadImage(
                info.cover, com.amigo.uibase.R.drawable.img_placehoder
            )
            viewBinding.ivProduct.tag = "${info.popCode}"
        } else {
            viewBinding.ivProduct.invisible()
            viewBinding.ivProduct.tag = null
        }
    }


}