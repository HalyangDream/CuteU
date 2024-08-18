package com.amigo.home.subevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amigo.analysis.Analysis
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseListFragment
import com.amigo.basic.ListIntent
import com.amigo.basic.ListState
import com.amigo.home.adapter.EventMeLikeAdapter
import com.amigo.home.databinding.FragmentEventMylikeBinding
import com.amigo.home.intent.FeedEventIntent
import com.amigo.home.state.FeedEventState
import com.amigo.home.viewmodel.EventViewModel
import com.amigo.logic.http.response.list.LikeMe
import com.amigo.tool.EventBus
import com.amigo.uibase.event.FollowerUnReadEvent

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