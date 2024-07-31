package com.cute.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.home.intent.AnchorDetailIntent
import com.cute.home.state.AnchorDetailState
import com.cute.logic.http.model.BehaviorRepository
import com.cute.logic.http.model.UserRepository
import com.cute.tool.EventBus
import com.cute.uibase.event.FollowBehaviorEvent
import kotlinx.coroutines.launch

class AnchorDetailViewModel : BaseMVIModel<AnchorDetailIntent, AnchorDetailState>() {

    private val _repository = UserRepository()
    private val _behaviorRepository by lazy { BehaviorRepository() }


    override fun processIntent(intent: AnchorDetailIntent) {
        when (intent) {
            is AnchorDetailIntent.GetAnchorInfo -> {
                getAnchorInfo(intent.anchorId)
            }

            is AnchorDetailIntent.Follow -> {
                handleLikeIntent(intent.anchorId)
            }

            is AnchorDetailIntent.UnFollow -> {
                handleUnLikeIntent(intent.anchorId)
            }

            is AnchorDetailIntent.BlockUser -> {
                blockUser(intent.peerId)
            }

            is AnchorDetailIntent.UnBlockUser -> {
                unBlockUser(intent.peerId)
            }

            is AnchorDetailIntent.ReportUser -> {
                reportUser(intent.peerId, intent.reportType)
            }
        }
    }

    private fun reportUser(peerId: Long, reportType: String) {
        viewModelScope.launch {
            val result = _behaviorRepository.reportUser(peerId, reportType)
            if (result.isSuccess) {
                setState(AnchorDetailState.ReportUserResult(true))
            } else {
                setState(AnchorDetailState.ReportUserResult(false))
            }
        }
    }


    private fun blockUser(peerId: Long) {
        viewModelScope.launch {
            val result = _behaviorRepository.blockUser(peerId)
            if (result.isSuccess) {
                setState(AnchorDetailState.BlockUserResult(true))
            } else {
                setState(AnchorDetailState.BlockUserResult(false))
            }
        }
    }

    private fun unBlockUser(peerId: Long) {
        viewModelScope.launch {
            val result = _behaviorRepository.unBlockUser(peerId)
            if (result.isSuccess) {
                setState(AnchorDetailState.UnBlockUserResult(true))
            } else {
                setState(AnchorDetailState.UnBlockUserResult(false))
            }
        }
    }

    private fun handleUnLikeIntent(anchorId: Long) {
        viewModelScope.launch {
            val response = _behaviorRepository.unFollowerUser(anchorId)
            if (response.isSuccess) {
                setState(AnchorDetailState.FollowState(false))
                EventBus.post(FollowBehaviorEvent.UnFollow(anchorId))
            }
        }
    }

    private fun handleLikeIntent(anchorId: Long) {
        viewModelScope.launch {
            val response = _behaviorRepository.followerUser(anchorId)
            if (response.isSuccess) {
                setState(AnchorDetailState.FollowState(true))
                EventBus.post(FollowBehaviorEvent.Follow(anchorId))
            }
        }
    }

    private fun getAnchorInfo(anchorId: Long) {
        viewModelScope.launch {
            val response = _repository.getUserDetail(anchorId)
            setState(AnchorDetailState.AnchorInfo(response.data))
        }
    }
}