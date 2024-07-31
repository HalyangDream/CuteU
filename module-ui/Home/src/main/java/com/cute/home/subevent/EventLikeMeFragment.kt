package com.cute.home.subevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cute.analysis.Analysis
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseListFragment
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.home.adapter.EventMeLikeAdapter
import com.cute.home.databinding.FragmentEventMylikeBinding
import com.cute.home.intent.FeedEventIntent
import com.cute.home.state.FeedEventState
import com.cute.home.viewmodel.EventViewModel
import com.cute.logic.http.response.list.LikeMe
import com.cute.tool.EventBus
import com.cute.uibase.event.FollowerUnReadEvent

class EventLikeMeFragment : BaseListFragment<FragmentEventMylikeBinding, EventViewModel>() {

    private var eventAdapter: EventMeLikeAdapter? = null

    override fun initViewBinding(
        layout: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventMylikeBinding {
        return FragmentEventMylikeBinding.inflate(layout, container, false)
    }

    override fun initView() {
        viewBinding.slLayout.setOnClickRefresh {
            viewModel.processIntent(FeedEventIntent.Loading("like"))
        }
        viewBinding.srlLayout.apply {
            this.setOnRefreshListener {
                viewModel.processIntent(FeedEventIntent.List(ListIntent.Refresh("like")))

            }
            this.setOnLoadMoreListener {
                viewModel.processIntent(FeedEventIntent.List(ListIntent.LoadMore("like")))
            }
        }
        viewBinding.rvMyLike.apply {
            eventAdapter = context?.let { EventMeLikeAdapter(it) }
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = eventAdapter
        }

        viewModel.observerState {
            if (it is FeedEventState.ListData) {
                handleListIntent(it.state)
            }
        }
    }

    override fun firstShowUserVisible() {
    }

    override fun onResume() {
        super.onResume()
        viewBinding.srlLayout.autoRefresh()
    }

    override fun onPause() {
        super.onPause()
        updateFollowerUnReadCount()
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
                eventAdapter?.setData(state.data as MutableList<LikeMe>?)
            }

            is ListState.LoadingSuccess -> {
                eventAdapter?.setData(state.data as MutableList<LikeMe>?)
            }

            is ListState.LoadMoreSuccess -> {
                viewBinding.srlLayout.finishLoadMore()
                eventAdapter?.addData(state.data as MutableList<LikeMe>?)
            }

            else -> super.handleListIntent(state)
        }
    }


    private fun updateFollowerUnReadCount() {
        context?.let {
            val count = it.userDataStore.getLikeMeUnReadCount()
            if (count > 0) {
                it.userDataStore.saveLikeMeUnReadCount(0)
                EventBus.post(FollowerUnReadEvent(0))
            }
        }

    }
}