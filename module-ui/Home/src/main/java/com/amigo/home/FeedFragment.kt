package com.amigo.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseListFragment
import com.amigo.basic.ListIntent
import com.amigo.basic.ListState
import com.amigo.basic.util.StatusUtils
import com.amigo.home.adapter.FeedAdapter
import com.amigo.home.databinding.FragmentFeedBinding
import com.amigo.home.intent.FeedIntent
import com.amigo.home.state.FeedState
import com.amigo.home.viewmodel.FeedViewModel
import com.amigo.logic.http.response.list.Feed
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.uibase.event.FollowBehaviorEvent
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.route.provider.ITelephoneService
import com.amigo.uibase.userbehavior.UserBehavior
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.BaseQuickAdapter

@Route(path = RoutePage.HOME.FEED_FRAGMENT)
class FeedFragment : BaseListFragment<FragmentFeedBinding, FeedViewModel>() {

    private var feedAdapter: FeedAdapter? = null
    private val telephoneService = RouteSdk.findService(ITelephoneService::class.java)
    override fun initViewBinding(
        layout: LayoutInflater, container: ViewGroup?
    ): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(layout, container, false)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.rlTitle, this)
        viewBinding.slLayout.setOnClickRefresh {
            viewModel.processIntent(FeedIntent.List(ListIntent.Loading()))
        }
        viewBinding.srlLayout.apply {
            this.setOnRefreshListener {
                viewModel.processIntent(FeedIntent.List(ListIntent.Refresh()))

            }
            this.setOnLoadMoreListener {
                viewModel.processIntent(FeedIntent.List(ListIntent.LoadMore()))
            }
        }
        viewBinding.rvUser.apply {
            feedAdapter = context?.let { FeedAdapter(it) }
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = feedAdapter
        }

        feedAdapter?.addOnItemChildClickListener(
            R.id.iv_call
        ) { adapter, view, position ->
            val item = adapter.getItem(position) ?: return@addOnItemChildClickListener
            // 拨打电话
            context?.let {
                telephoneService.sendCallInvited(
                    it, it.userDataStore.getUid(), item.id, "anchor_list"
                )
            }
            UserBehavior.setChargeSource("anchor_list_call")
        }

        viewModel.observerState {
            if (it is FeedState.ListData) {
                handleListIntent(it.state)
            }
        }
        EventBus.event.subscribe<FollowBehaviorEvent>(lifecycleScope) {
            when (it) {
                is FollowBehaviorEvent.Follow -> feedAdapter?.handleLikeState(it.id, true)

                is FollowBehaviorEvent.UnFollow -> feedAdapter?.handleLikeState(it.id, false)
            }
        }
    }

    override fun firstShowUserVisible() {
        viewModel.processIntent(FeedIntent.List(ListIntent.Loading()))
    }

    override fun emptyLayout(isEmpty: Boolean) {
        viewBinding.slLayout.showEmptyView(isEmpty)
    }

    override fun netErrorLayout(isNetError: Boolean) {
        viewBinding.slLayout.showNetErrorView(isNetError)
    }

    override fun dataHasBottomLayout(isBottom: Boolean) {
        viewBinding.srlLayout.setEnableLoadMore(!isBottom)
        feedAdapter?.showFooter(isBottom)
    }

    override fun loadingLayout(isLoading: Boolean) {
        viewBinding.slLayout.showLoadingView(isLoading)
    }

    override fun <T> handleListIntent(state: ListState<T>) {
        when (state) {
            is ListState.RefreshSuccess -> {
                viewBinding.srlLayout.finishRefresh()
                feedAdapter?.setData(state.data as MutableList<Feed>?)
            }

            is ListState.LoadingSuccess -> {
                feedAdapter?.setData(state.data as MutableList<Feed>?)
            }

            is ListState.LoadMoreSuccess -> {
                viewBinding.srlLayout.finishLoadMore()
                feedAdapter?.addData(state.data as MutableList<Feed>?)
            }

            else -> super.handleListIntent(state)
        }
    }
}