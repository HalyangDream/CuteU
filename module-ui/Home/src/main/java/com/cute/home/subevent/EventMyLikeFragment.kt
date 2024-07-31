package com.cute.home.subevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cute.analysis.Analysis
import com.cute.basic.BaseListFragment
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.home.adapter.EventLikeMeAdapter
import com.cute.home.databinding.FragmentEventLikeBinding
import com.cute.home.intent.FeedEventIntent
import com.cute.home.state.FeedEventState
import com.cute.home.viewmodel.EventViewModel
import com.cute.logic.http.response.list.MyLike
import com.cute.tool.EventBus
import com.cute.tool.EventBus.subscribe
import com.cute.uibase.event.RemoteNotifyEvent

class EventMyLikeFragment : BaseListFragment<FragmentEventLikeBinding, EventViewModel>() {

    private var eventAdapter: EventLikeMeAdapter? = null

    override fun initViewBinding(
        layout: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventLikeBinding {
        return FragmentEventLikeBinding.inflate(layout, container, false)
    }

    override fun initView() {

        viewBinding.slLayout.setOnClickRefresh {
            viewModel.processIntent(FeedEventIntent.Loading("my_like"))
        }

        viewBinding.srlLayout.apply {
            this.setOnRefreshListener {
                viewModel.processIntent(FeedEventIntent.List(ListIntent.Refresh("my_like")))

            }
            this.setOnLoadMoreListener {
                viewModel.processIntent(FeedEventIntent.List(ListIntent.LoadMore("my_like")))
            }
        }
        viewBinding.rvLike.apply {
            eventAdapter = context?.let { EventLikeMeAdapter(it) }
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = eventAdapter
        }

        EventBus.event.subscribe<RemoteNotifyEvent>(lifecycleScope) {
            if (it is RemoteNotifyEvent.PaySuccessEvent) {
                viewModel.processIntent(FeedEventIntent.List(ListIntent.Refresh("my_like")))
            }
        }

        viewModel.observerState {
            if (it is FeedEventState.ListData) {
                handleListIntent(it.state)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewBinding.srlLayout.autoRefresh()
    }


    override fun firstShowUserVisible() {
    }

    override fun emptyLayout(isEmpty: Boolean) {
        viewBinding.slLayout.showEmptyView(isEmpty)
    }

    override fun netErrorLayout(isNetError: Boolean) {
        viewBinding.slLayout.showNetErrorView(isNetError)
    }

    override fun dataHasBottomLayout(isBottom: Boolean) {
        viewBinding.srlLayout.setEnableLoadMore(!isBottom)
        eventAdapter?.showFooter(isBottom)

    }

    override fun loadingLayout(isLoading: Boolean) {
        viewBinding.slLayout.showLoadingView(isLoading)
    }

    override fun <T> handleListIntent(state: ListState<T>) {
        when (state) {
            is ListState.RefreshSuccess -> {
                viewBinding.srlLayout.finishRefresh()
                eventAdapter?.setData(state.data as MutableList<MyLike>?)
            }

            is ListState.LoadingSuccess -> {
                eventAdapter?.setData(state.data as MutableList<MyLike>?)
            }

            is ListState.LoadMoreSuccess -> {
                viewBinding.srlLayout.finishLoadMore()
                eventAdapter?.addData(state.data as MutableList<MyLike>?)
            }

            else -> super.handleListIntent(state)
        }
    }
}