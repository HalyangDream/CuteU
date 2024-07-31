package com.cute.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.home.bean.VideoFilterCondition
import com.cute.home.intent.FeedVideoIntent
import com.cute.home.state.FeedVideoState
import com.cute.http.ApiResponse
import com.cute.logic.http.model.BehaviorRepository
import com.cute.logic.http.model.ListRepository
import com.cute.logic.http.response.list.VideoListResponse
import com.cute.tool.EventBus
import com.cute.uibase.event.FollowBehaviorEvent
import kotlinx.coroutines.launch

class FeedVideoViewModel : BaseMVIModel<FeedVideoIntent, FeedVideoState>() {

    private val _listRepository by lazy { ListRepository() }
    private val _behaviorRepository by lazy { BehaviorRepository() }

    private var page = 1

    override fun processIntent(intent: FeedVideoIntent) {
        when (intent) {
            is FeedVideoIntent.List -> handleListIntent(intent.intent)
            is FeedVideoIntent.Follow -> handleLikeIntent(intent)
            is FeedVideoIntent.UnFollow -> handleUnLikeIntent(intent)
        }
    }

    private fun handleUnLikeIntent(intent: FeedVideoIntent.UnFollow) {
        viewModelScope.launch {
            val response = _behaviorRepository.unFollowerUser(intent.id)
            if (response.isSuccess) {
                EventBus.post(FollowBehaviorEvent.UnFollow(intent.id))
            }
        }
    }

    private fun handleLikeIntent(intent: FeedVideoIntent.Follow) {
        viewModelScope.launch {
            val response = _behaviorRepository.followerUser(intent.id)
            if (response.isSuccess) {
                EventBus.post(FollowBehaviorEvent.Follow(intent.id))
            }
        }
    }


    private fun handleListIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.Loading -> {
                loadingFirstData(intent.param as VideoFilterCondition)
            }

            is ListIntent.Refresh -> {
                refreshData(intent.param as VideoFilterCondition)
            }

            is ListIntent.LoadMore -> {
                loadMore(intent.param as VideoFilterCondition)
            }
        }
    }

    private fun loadingFirstData(filterCondition: VideoFilterCondition) {
        viewModelScope.launch {
            setState(FeedVideoState.ListData(ListState.Loading(isLoading = true)))
            page = 1

            val response = _listRepository.getVideoList(
                page,
                feeling = filterCondition.feeling?.id,
                language = filterCondition.language?.id,
                region = filterCondition.region?.id,
                country = filterCondition.country?.id
            )
            handleFocusFeedList(response)
            setState(FeedVideoState.ListData(ListState.Loading(isLoading = false)))
            setState(FeedVideoState.ListData(ListState.LoadingSuccess(response.data?.list)))
        }
    }


    private fun refreshData(filterCondition: VideoFilterCondition) {
        viewModelScope.launch {
            page = 1
            val response = _listRepository.getVideoList(
                page,
                feeling = filterCondition.feeling?.id,
                language = filterCondition.language?.id,
                region = filterCondition.region?.id,
                country = filterCondition.country?.id
            )
            handleFocusFeedList(response)
            setState(FeedVideoState.ListData(ListState.RefreshSuccess(response.data?.list)))
        }
    }

    private fun loadMore(filterCondition: VideoFilterCondition) {
        viewModelScope.launch {
            page++
            val response = _listRepository.getVideoList(
                page,
                feeling = filterCondition.feeling?.id,
                language = filterCondition.language?.id,
                region = filterCondition.region?.id,
                country = filterCondition.country?.id
            )
            val list = response.data?.list
            setState(FeedVideoState.ListData(ListState.LoadMoreSuccess(response.data?.list)))
            if (list.isNullOrEmpty()) {
                page--
            }
        }
    }

    /**
     * 处理统一的数据
     */
    private suspend fun handleFocusFeedList(response: ApiResponse<VideoListResponse>) {
        if (response.isSuccess) {
            val isEmpty = response.data?.list.isNullOrEmpty()
            if (!isEmpty) {
                val isBottom = response.data?.list?.size != 20
                setState(FeedVideoState.ListData(ListState.DataHasBottom(isBottom = isBottom)))
            }
            setState(FeedVideoState.ListData(ListState.EmptyData(isEmpty = isEmpty)))
            setState(FeedVideoState.ListData(ListState.NetError(isNetError = false)))
        } else {
            setState(FeedVideoState.ListData(ListState.NetError(isNetError = true)))
        }
    }
}