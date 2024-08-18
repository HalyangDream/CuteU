package com.amigo.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.amigo.baselogic.userDataStore
import com.amigo.basic.dialog.BaseBottomDialog
import com.amigo.home.databinding.DialogVideoFilterBinding
import com.amigo.logic.http.response.list.Filter
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.uibase.adapter.ViewPagerAdapter
import com.amigo.uibase.event.VideoFilterEvent
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.IStoreService
import com.amigo.uibase.userbehavior.UserBehavior
import com.google.android.material.tabs.TabLayout

class VideoFilterDialog : BaseBottomDialog() {

    private lateinit var binding: DialogVideoFilterBinding

    private var feeling: Filter? = null
    private var language: Filter? = null
    private var country: Filter? = null
    private var region: Filter? = null

    private var listener: ((feeling: Filter?, language: Filter?, country: Filter?, region: Filter?) -> Unit)? =
        null

    override fun parseBundle(bundle: Bundle?) {

    }

    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View? {
        binding = DialogVideoFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView(view: View?) {
        setDialogCancelable(false)
        setDialogCanceledOnTouchOutside(false)
        binding.ivClose.setOnClickListener {
            dismissDialog()
        }
        binding.rlUnlockVip.setOnClickListener {
            UserBehavior.setChargeSource("filter")
            val iStoreService = RouteSdk.findService(IStoreService::class.java)
            iStoreService.showCodeDialog("20200", null)
        }

        binding.btnConfirm.setOnClickListener {
            val isVip = it.context.userDataStore.readVip()
            if (isVip) {
                listener?.invoke(feeling, language, country, region)
                dismissDialog()
            } else {
                UserBehavior.setChargeSource("filter")
                val iStoreService = RouteSdk.findService(IStoreService::class.java)
                iStoreService.showCodeDialog("20200", null)
            }
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab) {
                binding.vp.currentItem = p0.position
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })
        val fragments = mutableListOf(DialogCrowdFragment(), DialogCountryRegionFragment())
        val pagerAdapter = ViewPagerAdapter(childFragmentManager, lifecycle, fragments)
        binding.vp.isUserInputEnabled = false
        binding.vp.isSaveEnabled = false
        binding.vp.offscreenPageLimit = fragments.size
        binding.vp.adapter = pagerAdapter
        binding.vp.currentItem = 0
        EventBus.event.subscribe<VideoFilterEvent>(lifecycleScope) {
            when (it) {
                is VideoFilterEvent.FeelingFilterEvent -> feeling = it.filter
                is VideoFilterEvent.LanguageFilterEvent -> language = it.filter
                is VideoFilterEvent.CountryFilterEvent -> country = it.filter
                is VideoFilterEvent.RegionFilterEvent -> region = it.filter
            }
        }
    }

    override fun initData() {
        val tabTitle = arrayOf(
            context?.getString(com.amigo.uibase.R.string.str_crowd),
            context?.getString(com.amigo.uibase.R.string.str_country_region)
        )
        for (s in tabTitle) {
            val tab = binding.tabLayout.newTab()
            tab.text = s
            binding.tabLayout.addTab(tab)
        }
    }

    fun setVideoFilterListener(listener: ((feeling: Filter?, language: Filter?, country: Filter?, region: Filter?) -> Unit)?) {
        this.listener = listener
    }

}