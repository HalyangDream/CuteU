package com.amigo.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.basic.ListIntent
import com.amigo.basic.ListState
import com.amigo.home.intent.FeedEventIntent
import com.amigo.home.state.FeedEventState
import com.amigo.http.ApiResponse
import com.amigo.logic.http.model.ListRepository
import kotlinx.coroutines.launch

class EventViewModel : BaseMVIModel<FeedEventIntent, FeedEventState>() {

    private val _listRepository by lazy { ListRepository() }

    private var page = 1

    override fun processIntent(intent: FeedEventIntent) {
        when (intent) {
            is FeedEventIntent.Loading -> loadingFirstData(intent.source)
            is FeedEventIntent.List -> handleListIntent(intent.intent)
        }
    }

    private fun handleListIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.Refresh -> {
                val source = intent.param as String
                refreshData(source)
            }

            is ListIntent.LoadMore -> {
                val source = intent.param as String
                loadMore(source)
            }

            else -> {}
        }
    }

    private suspend fun getRequestBySource(source: String): Pair<Boolean, MutableList<*>?> {
        when (source) {
            "like" -> {
                val apiResponse = _listRepository.getLikeMeList(page)
                return Pair(apiResponse.isSuccess, apiResponse.data?.list)
            }

            "my_like" -> {
                val apiResponse = _listRepository.getMyLikeList(page)
                return Pair(apiResponse.isSuccess, apiResponse.data?.list)
            }

            else -> return Pair(false, null)
        }
    }


    private fun loadingFirstData(source: String) {
        viewModelScope.launch {
            setState(FeedEventState.ListData(ListState.Loading(isLoading = true)))
            page = 1
            val response = getRequestBySource(source)
            handleFocusFeedList(response.first, response.second)
            val list = response.second
            setState(FeedEventState.ListData(ListState.LoadingSuccess(list)))
            setState(FeedEventState.ListData(ListState.Loading(isLoading = false)))
        }
    }

    private fun refreshData(source: String) {
        viewModelScope.launch {
            page = 1
            val response = getRequestBySource(source)
            handleFocusFeedList(response.first, response.second)
            setState(FeedEventState.ListData(ListState.RefreshSuccess(response.second)))
        }
    }

    private fun loadMore(source: String) {
        viewModelScope.launch {
            page++
            val response = getRequestBySource(source)
            val list = response.second
            setState(FeedEventState.ListData(ListState.LoadMoreSuccess(list)))
            val isBottom = list?.size != 20
            setState(FeedEventState.ListData(ListState.DataHasBottom(isBottom = isBottom)))
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
                setState(FeedEventState.ListData(ListState.DataHasBottom(isBottom = isBottom)))
            }
            setState(FeedEventState.ListData(ListState.EmptyData(isEmpty = isEmpty)))
            setState(FeedEventState.ListData(ListState.NetError(isNetError = false)))
        } else {
            setState(FeedEventState.ListData(ListState.NetError(isNetError = true)))
        }
    }
}