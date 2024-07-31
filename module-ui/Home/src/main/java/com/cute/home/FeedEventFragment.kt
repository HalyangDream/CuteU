package com.cute.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.hjq.shape.view.ShapeTextView
import com.cute.baselogic.promptDateStore
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseFragment
import com.cute.basic.util.StatusUtils
import com.cute.home.databinding.FragmentFeedEventBinding
import com.cute.home.subevent.EventMyLikeFragment
import com.cute.home.subevent.EventLikeMeFragment
import com.cute.im.IMCore
import com.cute.im.bean.Conversation
import com.cute.im.listener.ConversationListener
import com.cute.im.service.ConversationService
import com.cute.im.service.MsgServiceObserver
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.dpToPx
import com.cute.uibase.adapter.ViewPagerAdapter
import com.cute.uibase.dialog.AppAlertDialog
import com.cute.uibase.event.FollowerUnReadEvent
import com.cute.uibase.invisible
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.userbehavior.UserBehavior
import com.cute.uibase.visible
import kotlinx.coroutines.launch

@Route(path = RoutePage.HOME.FEED_EVENT_FRAGMENT)
class FeedEventFragment : BaseFragment<FragmentFeedEventBinding>(), ConversationListener {


    private val defaultPage = 0


    override fun initViewBinding(
        layout: LayoutInflater,
        container: ViewGroup?
    ): FragmentFeedEventBinding {
        return FragmentFeedEventBinding.inflate(layout, container, false)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.rlTitle, this)
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, true)
        setupViewPager()
        setupTabLayout()
        viewBinding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    setupTabItemLayout(true, tab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    setupTabItemLayout(false, tab)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        EventBus.event.subscribe<FollowerUnReadEvent>(lifecycleScope) {
            getLikesUnreadCount()
        }
    }

    override fun firstShowUserVisible() {
        context?.apply {
            getMsgUnreadCount()
            getLikesUnreadCount()
            applyNotifyPermissionDialog(this)
        }
    }

    override fun onConversationChange(conversation: Conversation) {
        getMsgUnreadCount()
    }

    override fun onConversationDelete(list: MutableList<Conversation>) {
        getMsgUnreadCount()
    }

    override fun onResume() {
        super.onResume()
        UserBehavior.setRootPage("event_page")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        IMCore.getService(MsgServiceObserver::class.java).observerConversationChange(this, false)
    }


    private fun setupViewPager() {
        val messageFragment = RouteSdk.getNavigationFragment(RoutePage.CHAT.CHAT_CONVERSATION)
        val meLikeFragment = EventMyLikeFragment()
        val likeMeFragment = EventLikeMeFragment()
        val fragments =
            mutableListOf(messageFragment, likeMeFragment, meLikeFragment)
        val pagerAdapter =
            ViewPagerAdapter(childFragmentManager, lifecycle, fragments)
        viewBinding.vpEvent.adapter = pagerAdapter
        viewBinding.vpEvent.isUserInputEnabled = false
        viewBinding.vpEvent.offscreenPageLimit = fragments.size
        viewBinding.vpEvent.setCurrentItem(defaultPage, false)
    }

    private fun setupTabLayout() {
        val tabData = arrayOf(
            context?.getString(com.cute.uibase.R.string.str_message),
            context?.getString(com.cute.uibase.R.string.str_likes),
            context?.getString(com.cute.uibase.R.string.str_my_likes)
        )
        val mediator = TabLayoutMediator(
            viewBinding.tabLayout,
            viewBinding.vpEvent,
            false, false
        ) { tab, position ->
            initTabItemLayout(position, tabData[position] ?: "", tab)
            if (position == defaultPage) {
                setupTabItemLayout(true, tab)
            }
        }
        mediator.attach()
    }

    private fun initTabItemLayout(position: Int, title: String, tab: TabLayout.Tab) {
        tab.setCustomView(R.layout.tab_event)
        val tabContext = viewBinding.tabLayout.context
        val textView = tab.customView?.findViewById<ShapeTextView>(R.id.stv_tab)
        textView?.text = title
        if (position == 0) {
            textView?.textSize = 32f
            val selectColor =
                ContextCompat.getColor(tabContext, com.cute.uibase.R.color.color_tab_event_select)
            textView?.setTextColor(selectColor)
        }else{
            val defColor =
                ContextCompat.getColor(tabContext, com.cute.uibase.R.color.color_tab_event_def)
            textView?.textSize = 18f
            textView?.setTextColor(defColor)
        }
    }

    private fun setupTabItemLayout(isSelect: Boolean, tab: TabLayout.Tab) {
        val tabContext = viewBinding.tabLayout.context
        val textView = tab.customView!!.findViewById<ShapeTextView>(R.id.stv_tab)
        if (isSelect) {
            textView.textSize = 32f
            val selectColor =
                ContextCompat.getColor(tabContext, com.cute.uibase.R.color.color_tab_event_select)
            textView.setTextColor(selectColor)
        } else {
            val defColor =
                ContextCompat.getColor(tabContext, com.cute.uibase.R.color.color_tab_event_def)
            textView.textSize = 18f
            textView.setTextColor(defColor)
        }


//
//        val selectTextColor =
//            ContextCompat.getColor(tabContext, com.cute.uibase.R.color.app_text_reverse_color)
//        val defTextColor =
//            ContextCompat.getColor(tabContext, com.cute.uibase.R.color.app_text_color)
//
//
//        val textView = tab.customView!!.findViewById<ShapeTextView>(R.id.stv_tab)
//        textView.shapeDrawableBuilder
//            .setSolidColor(if (isSelect) selectColor else defColor)
//            .setRadius(14.dpToPx(tabContext).toFloat())
//            .intoBackground()
//        textView.setTextColor(if (isSelect) selectTextColor else defTextColor)
    }


    private fun getMsgUnreadCount() {
        val uid = context?.userDataStore?.getUid()
        lifecycleScope.launch {
            if (uid != null) {
                val count =
                    IMCore.getService(ConversationService::class.java).getUnReadCount("$uid")
                setupTabItemUnRead(0, count > 0, count)
            }
        }
    }

    private fun getLikesUnreadCount() {
        val followerUnreadCount = context?.userDataStore?.getLikeMeUnReadCount()
        if (followerUnreadCount != null) {
            setupTabItemUnRead(1, followerUnreadCount > 0, followerUnreadCount)
        }
    }

    private fun setupTabItemUnRead(position: Int, isVisible: Boolean, count: Int? = null) {
        val tab = viewBinding.tabLayout.getTabAt(position)
        val unReadView = tab?.customView?.findViewById<ShapeTextView>(R.id.stv_unread)
        if (isVisible) {
            unReadView?.visible()
            if (count == null) {
                unReadView?.text = ""
            } else {
                val newCount = if (count > 99) 99 else count
                unReadView?.text = "($newCount)"
            }
        } else {
            unReadView?.invisible()
        }
    }

    private fun applyNotifyPermissionDialog(context: Context) {
        val hasPermission = hasNotifyPermission(context)
        if (hasPermission) return
        val isPrompt = context.promptDateStore.promptNotifyPermission()
        if (!isPrompt) return
        val alertDialog =
            AppAlertDialog.Builder()
                .setTitle(context.getString(com.cute.uibase.R.string.str_enable_notify))
                .setContent(context.getString(com.cute.uibase.R.string.str_enable_notify_desc))
                .setBtnPositiveContent(context.getString(com.cute.uibase.R.string.str_go_setting))
                .setBtnNegativeContent(context.getString(com.cute.uibase.R.string.str_cancel))
                .create(context)
        alertDialog.setPositiveListener {
            openNotificationSettingLayout(context)
        }
    }


    /**
     * 是否有通知栏权限
     */
    private fun hasNotifyPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        } else {
            val managerCompat = NotificationManagerCompat.from(context)
            managerCompat.areNotificationsEnabled()
        }
    }

    /**
     * 跳转到开启通知栏权限的界面
     */
    private fun openNotificationSettingLayout(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            requestMultiplePermission(
                android.Manifest.permission.POST_NOTIFICATIONS,
                onGranted = {},
                onDenied = {})
        } else {
            try {
                val intent = Intent()
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo?.uid)
                } else {
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo?.uid)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    }
}