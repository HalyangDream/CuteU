package com.cute.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.home.intent.FeedIntent
import com.cute.home.state.FeedState
import com.cute.logic.http.model.BehaviorRepository
import com.cute.logic.http.model.ListRepository
import com.cute.tool.EventBus
import com.cute.uibase.event.FollowBehaviorEvent
import kotlinx.coroutines.launch

class FeedViewModel : BaseMVIModel<FeedIntent, FeedState>() {

    private val _listRepository = ListRepository()
    private val _behaviorRepository = BehaviorRepository()
    private var page: Int = 1
    private var pageSize = 0
    override fun processIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.List -> handleListIntent(intent.intent)

            is FeedIntent.Follow -> handleLikeIntent(intent)
        }
    }

    private fun handleListIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.Loading -> loadingFirstData()

            is ListIntent.Refresh -> {
                refreshData()
            }

            is ListIntent.LoadMore -> {
                loadMore()
            }

            else -> {}
        }
    }

    private fun handleLikeIntent(intent: FeedIntent.Follow) {
        viewModelScope.launch {
            val response = _behaviorRepository.followerUser(intent.id)
            if (response.isSuccess) {
                EventBus.post(FollowBehaviorEvent.Follow(intent.id))
            }
        }
    }

    private fun loadingFirstData() {
        viewModelScope.launch {
            setState(FeedState.ListData(ListState.Loading(isLoading = true)))
            page = 1
            val response = _listRepository.getFeedList(page)
            handleFocusFeedList(response.isSuccess, response.data?.list)
            val list = response.data?.list
            pageSize = if (!list.isNullOrEmpty()) list.size else 0
            setState(FeedState.ListData(ListState.LoadingSuccess(list)))
            setState(FeedState.ListData(ListState.Loading(isLoading = false)))
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            page = 1
            val response = _listRepository.getFeedList(page)
            handleFocusFeedList(response.isSuccess, response.data?.list)
            setState(FeedState.ListData(ListState.RefreshSuccess(response.data?.list)))
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            page++
            val response = _listRepository.getFeedList(page)
            val list = response.data?.list
            setState(FeedState.ListData(ListState.LoadMoreSuccess(list)))
            val isBottom = if (response.isSuccess) {
                if (list.isNullOrEmpty()) true
                else if (pageSize != 0 && list.isNotEmpty()) list.size < pageSize
                else false
            } else false
            setState(FeedState.ListData(ListState.DataHasBottom(isBottom = isBottom)))
            if (list.isNullOrEmpty()) {
                page--
            }
        }
    }

    /**
     * 处理统一的数据
     */
    private suspend fun handleFocusFeedList(isSuccess: Boolean, response: MutableList<*>?) {
        if (isSuccess) {
            val isEmpty = response.isNullOrEmpty()
            if (!isEmpty) {
                val isBottom = response!!.size != 20
                setState(FeedState.ListData(ListState.DataHasBottom(isBottom = isBottom)))
            }
            setState(FeedState.ListData(ListState.EmptyData(isEmpty = isEmpty)))
            setState(FeedState.ListData(ListState.NetError(isNetError = false)))
        } else {
            setState(FeedState.ListData(ListState.NetError(isNetError = true)))
        }
    }


}