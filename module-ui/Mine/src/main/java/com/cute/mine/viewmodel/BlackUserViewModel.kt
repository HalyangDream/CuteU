package com.cute.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.basic.ListIntent
import com.cute.basic.ListState
import com.cute.http.ApiResponse
import com.cute.logic.http.model.BehaviorRepository
import com.cute.logic.http.model.ListRepository
import com.cute.logic.http.response.list.BlackListResponse
import com.cute.logic.http.response.list.BlackUser
import com.cute.mine.intent.BlackUserIntent
import com.cute.mine.state.BlackUserState
import kotlinx.coroutines.launch

class BlackUserViewModel : BaseMVIModel<BlackUserIntent, BlackUserState>() {


    private val _listRepository = ListRepository()
    private val _behaviorRepository = BehaviorRepository()
    private var page = 1

    override fun processIntent(intent: BlackUserIntent) {
        when (intent) {
            is BlackUserIntent.BlackList -> handleListIntent(intent.intent)
            is BlackUserIntent.RemoveBlack -> removeBlackList(intent.user)
        }
    }


    private fun handleListIntent(intent: ListIntent) {
        when (intent) {
            is ListIntent.Loading -> loadingFirstData()


            is ListIntent.Refresh -> refreshData()


            is ListIntent.LoadMore -> loadMore()

            else -> {}
        }
    }

    private fun loadingFirstData() {
        viewModelScope.launch {
            setState(BlackUserState.BlackListResult(ListState.Loading(isLoading = true)))
            page = 1
            val response = _listRepository.getBlackList(page)
            handleFocusBlackList(response)
            val list = response.data?.list
            setState(BlackUserState.BlackListResult(ListState.LoadingSuccess(list)))
            setState(BlackUserState.BlackListResult(ListState.Loading(isLoading = false)))
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            page = 1
            val response = _listRepository.getBlackList(page)
            handleFocusBlackList(response)
            setState(BlackUserState.BlackListResult(ListState.RefreshSuccess(response.data?.list)))
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            page++
            val response = _listRepository.getBlackList(page)
            val list = response.data?.list
            setState(BlackUserState.BlackListResult(ListState.LoadMoreSuccess(response.data?.list)))
            val isBottom = response.data?.list?.size != 20
            setState(BlackUserState.BlackListResult(ListState.DataHasBottom(isBottom = isBottom)))
            if (list.isNullOrEmpty()) {
                page--
            }
        }
    }


    /**
     * 处理统一的数据
     */
    private suspend fun handleFocusBlackList(response: ApiResponse<BlackListResponse>) {
        if (response.isSuccess) {
            val isEmpty = response.data?.list.isNullOrEmpty()
            if (!isEmpty) {
                val isBottom = response.data?.list?.size != 20
                setState(BlackUserState.BlackListResult(ListState.DataHasBottom(isBottom = isBottom)))
            }
            setState(BlackUserState.BlackListResult(ListState.EmptyData(isEmpty = isEmpty)))
            setState(BlackUserState.BlackListResult(ListState.NetError(isNetError = false)))
        } else {
            setState(BlackUserState.BlackListResult(ListState.NetError(isNetError = true)))
        }
    }


    private fun removeBlackList(user: BlackUser) {
        viewModelScope.launch {
            val response = _behaviorRepository.unBlockUser(user.id)
            if (response.isSuccess) {
                setState(BlackUserState.RemoveBlackSuccess(user))
            }
        }
    }
}