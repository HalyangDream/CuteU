package com.cute.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.cute.analysis.Analysis
import com.cute.basic.BaseListFragment
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.basic.util.StatusUtils
import com.cute.home.adapter.FeedVideoAdapter
import com.cute.home.bean.VideoFilterCondition
import com.cute.home.databinding.FragmentFeedVideoBinding
import com.cute.home.databinding.ItemFeedVideoBinding
import com.cute.home.dialog.VideoFilterDialog
import com.cute.home.intent.FeedVideoIntent
import com.cute.home.state.FeedVideoState
import com.cute.home.viewmodel.FeedVideoViewModel
import com.cute.logic.http.response.list.VideoList
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.tool.Toaster
import com.cute.uibase.ActivityStack
import com.cute.uibase.ReportBehavior
import com.cute.uibase.event.FollowBehaviorEvent
import com.cute.uibase.event.RefreshFeedVideoEvent
import com.cute.uibase.event.VideoFilterEvent
import com.cute.uibase.media.VideoPlayerManager
import com.cute.uibase.route.RoutePage
import com.cute.uibase.setThrottleListener
import com.cute.uibase.userbehavior.UserBehavior

@Route(path = RoutePage.HOME.FEED_VIDEO_FRAGMENT)
class FeedVideoFragment : BaseListFragment<FragmentFeedVideoBinding, FeedVideoViewModel>() {

    private var feedVideoAdapter: FeedVideoAdapter? = null
    private val videoFilterCondition = VideoFilterCondition()
    private val videoFilterDialog = VideoFilterDialog()


    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentFeedVideoBinding {
        return FragmentFeedVideoBinding.inflate(layout, container, false)
    }

    override fun initView() {
        context?.let {
            val layoutParams = viewBinding.ivFilter.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = StatusUtils.getStatusBarHeight(it)
        }
        viewBinding.ivFilter.setThrottleListener {
            videoFilterDialog.showDialog(it.context, null)
        }
        videoFilterDialog.setVideoFilterListener { feeling, language, country, region ->
            videoFilterCondition.feeling = feeling
            videoFilterCondition.language = language
            videoFilterCondition.country = country
            videoFilterCondition.region = region
            viewBinding.srlLayout.autoRefresh()
        }

        viewBinding.slLayout.setOnClickRefresh {
            viewModel.processIntent(FeedVideoIntent.List(ListIntent.Loading(videoFilterCondition)))
        }
        viewBinding.srlLayout.apply {
            setOnRefreshListener {
                viewModel.processIntent(FeedVideoIntent.List(ListIntent.Refresh(videoFilterCondition)))
            }

            setOnLoadMoreListener {
                viewModel.processIntent(
                    FeedVideoIntent.List(
                        ListIntent.LoadMore(
                            videoFilterCondition
                        )
                    )
                )
            }
        }

        viewBinding.vpVideo.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit = 5
            feedVideoAdapter = FeedVideoAdapter(context)
            adapter = feedVideoAdapter
        }
        viewBinding.vpVideo.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    playVideo(position)
                }
                autoLoadMore(position)
            }
        })

        feedVideoAdapter?.addOnItemChildClickListener(
            R.id.ll_like
        ) { adapter, _, position ->
            val item = adapter.getItem(position)
            item?.let {
                viewModel.processIntent(FeedVideoIntent.Follow(it.id))
            }
        }

        feedVideoAdapter?.addOnItemChildClickListener(
            R.id.iv_like_state
        ) { adapter, _, position ->
            val item = adapter.getItem(position)

            item?.let {
                if (it.isFollow) {
                    viewModel.processIntent(FeedVideoIntent.UnFollow(it.id))
                } else {
                    viewModel.processIntent(FeedVideoIntent.Follow(it.id))
                }
            }
        }

        EventBus.event.subscribe<FollowBehaviorEvent>(lifecycleScope) {
            when (it) {
                is FollowBehaviorEvent.Follow -> {
                    feedVideoAdapter?.handleLikeState(it.id, true)
                    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                        Toaster.showShort(
                            ActivityStack.application, com.cute.uibase.R.string.str_liked
                        )
                    }
                }

                is FollowBehaviorEvent.UnFollow -> {
                    feedVideoAdapter?.handleLikeState(it.id, false)
                    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                        Toaster.showShort(
                            ActivityStack.application, com.cute.uibase.R.string.str_unliked
                        )
                    }
                }
            }
        }

        EventBus.event.subscribe<RefreshFeedVideoEvent>(lifecycleScope) {
            feedVideoAdapter?.setData(null)
            viewModel.processIntent(FeedVideoIntent.List(ListIntent.Loading(videoFilterCondition)))
        }
        viewModel.observerState {
            if (it is FeedVideoState.ListData) {
                handleListIntent(it.state)
            }
        }
    }

    override fun firstShowUserVisible() {
        viewModel.processIntent(FeedVideoIntent.List(ListIntent.Loading(videoFilterCondition)))
    }


    override fun <T> handleListIntent(state: ListState<T>) {

        when (state) {
            is ListState.LoadingSuccess -> {
                context?.let { VideoPlayerManager.releaseDouYinHelperResource(it) }
                feedVideoAdapter?.setData(state.data as MutableList<VideoList>?)
            }

            is ListState.RefreshSuccess -> {
                context?.let { VideoPlayerManager.releaseDouYinHelperResource(it) }
                viewBinding.srlLayout.finishRefresh()
                feedVideoAdapter?.setData(state.data as MutableList<VideoList>?)
                viewBinding.vpVideo.post {
                    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                        playVideo(viewBinding.vpVideo.currentItem)
                    }
                }
            }

            is ListState.LoadMoreSuccess -> {
                viewBinding.srlLayout.finishLoadMore()
                feedVideoAdapter?.addData(state.data as MutableList<VideoList>?)
            }

            else -> super.handleListIntent(state)
        }
    }

    override fun emptyLayout(isEmpty: Boolean) {
        viewBinding.slLayout.showEmptyView(isEmpty)
    }

    override fun netErrorLayout(isNetError: Boolean) {
        viewBinding.slLayout.showNetErrorView(isNetError)
    }

    override fun dataHasBottomLayout(isBottom: Boolean) {
        if (isBottom) {
            viewBinding.srlLayout.setEnableLoadMore(false)
        } else {
            viewBinding.srlLayout.setEnableLoadMore(true)
        }
    }

    override fun loadingLayout(isLoading: Boolean) {
        viewBinding.slLayout.showLoadingView(isLoading)

    }

    override fun onResume() {
        super.onResume()
        resume()
        activity?.let { StatusUtils.setStatusMode(false, it.window) }
        UserBehavior.setRootPage("show_list")
        ReportBehavior.reportEvent("show_list_page")
    }

    override fun onPause() {
        super.onPause()
        activity?.let { StatusUtils.setStatusMode(true, it.window) }
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let { VideoPlayerManager.releaseDouYinPlayer(it) }
    }

    private fun autoLoadMore(position: Int) {
        if (feedVideoAdapter == null) return
        if (feedVideoAdapter!!.itemCount - position < 5) {
            viewModel.processIntent(FeedVideoIntent.List(ListIntent.LoadMore(videoFilterCondition)))
        }
    }

    private fun playVideo(position: Int) {
        val recyclerView = viewBinding.vpVideo.getChildAt(0) as RecyclerView
        val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView ?: return
        val itemBinding: ItemFeedVideoBinding = ItemFeedVideoBinding.bind(itemView)
        val item = feedVideoAdapter?.getItem(position) ?: return
        itemBinding.videoView.play(item.videoUrl)
        ReportBehavior.reportEvent("show_list_video", mutableMapOf<String, Any>().apply {
            put("anchor_id", "${item.id}")
            put("url", "${item.videoUrl}")
        })
    }

    private fun pause() {
        val position = viewBinding.vpVideo.currentItem
        val recyclerView = viewBinding.vpVideo.getChildAt(0) as RecyclerView
        val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView ?: return
        val itemBinding: ItemFeedVideoBinding = ItemFeedVideoBinding.bind(itemView)
        itemBinding.videoView.pause()
    }

    private fun resume() {
        val adapter = viewBinding.vpVideo.adapter
        if (adapter == null || adapter.itemCount <= 0) return
        val position = viewBinding.vpVideo.currentItem
        playVideo(position)
    }
}