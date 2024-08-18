package com.amigo.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.home.intent.AnchorDetailIntent
import com.amigo.home.state.AnchorDetailState
import com.amigo.logic.http.model.BehaviorRepository
import com.amigo.logic.http.model.UserRepository
import com.amigo.tool.EventBus
import com.amigo.uibase.event.FollowBehaviorEvent
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