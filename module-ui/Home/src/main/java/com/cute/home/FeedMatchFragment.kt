package com.cute.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseModelFragment
import com.cute.basic.util.StatusUtils
import com.cute.home.databinding.FragmentFeedMatchBinding
import com.cute.home.dialog.MatchOptionDialog
import com.cute.home.intent.FeedMatchIntent
import com.cute.home.state.FeedMatchState
import com.cute.home.viewmodel.FeedMatchViewModel
import com.cute.logic.http.response.list.MatchOption
import com.cute.picture.loadImage
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.uibase.ReportBehavior
import com.cute.uibase.event.GetRewardEvent
import com.cute.uibase.invisible
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.route.provider.ITelephoneService
import com.cute.uibase.setThrottleListener
import com.cute.uibase.visible
import kotlinx.coroutines.delay

@Route(path = RoutePage.HOME.FEED_MATCH_FRAGMENT)
class FeedMatchFragment : BaseModelFragment<FragmentFeedMatchBinding, FeedMatchViewModel>() {


    private val iTelephoneService by lazy(LazyThreadSafetyMode.NONE) {
        RouteSdk.findService(
            ITelephoneService::class.java
        )
    }
    private var matchOptions: MatchOption? = null
    private val matchOptionDialog by lazy { MatchOptionDialog() }
    private var dialogOffset = 0


    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentFeedMatchBinding {
        return FragmentFeedMatchBinding.inflate(layout, container, false)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.flTitle, this)

        viewBinding.cameraPreview.setThrottleListener {
            if (matchOptions != null) {
                val uid = it.context.userDataStore.getUid()
                iTelephoneService.launchMatch(uid, matchOptions!!.id, "match")
                ReportBehavior.reportEvent("matching_page")
            }
        }

        viewBinding.sllMatch.setOnClickListener {
            if (dialogOffset == 0) {
                val statusHeight = StatusUtils.getStatusBarHeight(it.context)
                val pTop = viewBinding.sllMatch.top
                val bottom = viewBinding.sllMatch.bottom
                Log.i("FeedMatch", "bottom:$bottom")
                dialogOffset = pTop + statusHeight
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val displayCutout = it.display.cutout
                    if (displayCutout != null) {
                        dialogOffset -= statusHeight
                    }
                }
            }

            if (matchOptions != null) {
                matchOptionDialog.setOffsetTop(dialogOffset)
                matchOptionDialog.showDialog(it.context, null)
            }
        }
        matchOptionDialog.setSelectorListener {
            this.matchOptions = it
            matchOptionDialog.setSelector(it)
//            viewBinding.ivMatchType.loadImage(matchOptions!!.icon)
            viewBinding.tvMatchType.text = "${matchOptions!!.name}"
        }

        viewModel.observerState {
            when (it) {

                is FeedMatchState.ProfileResult -> {
                    val freeMatchNum = it.profile.freeMatchNum
                    if (freeMatchNum.isNullOrEmpty() || freeMatchNum == "0") {
                        viewBinding.llFree.invisible()
                    } else {
                        viewBinding.llFree.visible()
                        viewBinding.stvFreeMatchNum.text =
                            "${context?.getString(com.cute.uibase.R.string.str_free)}: x${freeMatchNum}"
                    }
                }

                is FeedMatchState.MatchOptionResult -> bindMatchOption(it.list)
            }
        }
        EventBus.event.subscribe<GetRewardEvent>(lifecycleScope) {
            delay(1000)
            viewModel.processIntent(FeedMatchIntent.ReqProfile)
            viewModel.processIntent(FeedMatchIntent.ReqMatchOption)
        }
    }


    override fun firstShowUserVisible() {

    }

    override fun onResume() {
        super.onResume()
        viewModel.processIntent(FeedMatchIntent.ReqProfile)
        viewModel.processIntent(FeedMatchIntent.ReqMatchOption)
        ReportBehavior.reportEvent("match_list_page")
        requestMultiplePermission(android.Manifest.permission.CAMERA, onGranted = {
            viewBinding.cameraPreview.launchCamera(this, true)
        }, onDenied = {})

    }

    override fun onPause() {
        super.onPause()
        viewBinding.cameraPreview.release()
    }

    private fun bindMatchOption(selectors: MutableList<MatchOption>) {
        val index = isContainsIndex(matchOptions, selectors)
        matchOptions = if (index == -1) selectors[0] else selectors[index]
        matchOptionDialog.setSelectors(selectors)
        matchOptionDialog.setSelector(matchOptions!!)
//        viewBinding.ivMatchType.loadImage(matchOptions!!.icon)
        viewBinding.tvMatchType.text = "${matchOptions!!.name}"
    }

    private fun isContainsIndex(item: MatchOption?, selectors: MutableList<MatchOption>): Int {
        if (item == null) return -1
        for ((index, matchSelector) in selectors.withIndex()) {
            if (matchSelector.id == item.id) return index
        }
        return -1
    }

}