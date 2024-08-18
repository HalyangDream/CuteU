package com.amigo.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelFragment
import com.amigo.basic.util.StatusUtils
import com.amigo.home.databinding.FragmentFeedMatchBinding
import com.amigo.home.dialog.MatchOptionDialog
import com.amigo.home.intent.FeedMatchIntent
import com.amigo.home.state.FeedMatchState
import com.amigo.home.viewmodel.FeedMatchViewModel
import com.amigo.logic.http.response.list.MatchOption
import com.amigo.picture.loadImage
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.uibase.ReportBehavior
import com.amigo.uibase.event.GetRewardEvent
import com.amigo.uibase.invisible
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.setThrottleListener
import com.amigo.uibase.visible
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
                            "${context?.getString(com.amigo.uibase.R.string.str_free)}: x${freeMatchNum}"
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