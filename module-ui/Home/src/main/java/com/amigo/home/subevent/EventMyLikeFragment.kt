package com.amigo.home.subevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amigo.analysis.Analysis
import com.amigo.basic.BaseListFragment
import com.amigo.basic.ListIntent
import com.amigo.basic.ListState
import com.amigo.home.adapter.EventLikeMeAdapter
import com.amigo.home.databinding.FragmentEventLikeBinding
import com.amigo.home.intent.FeedEventIntent
import com.amigo.home.state.FeedEventState
import com.amigo.home.viewmodel.EventViewModel
import com.amigo.logic.http.response.list.MyLike
import com.amigo.tool.EventBus
import com.amigo.tool.EventBus.subscribe
import com.amigo.uibase.event.RemoteNotifyEvent

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