package com.amigo.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.home.intent.FeedMatchIntent
import com.amigo.home.state.FeedMatchState
import com.amigo.logic.http.model.ListRepository
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.logic.http.response.list.Feed
import kotlinx.coroutines.launch

class FeedMatchViewModel : BaseMVIModel<FeedMatchIntent, FeedMatchState>() {


    private val _listRepository = ListRepository()
    private val _profileRepository = ProfileRepository()
    private var page = 1
    private var fromFirst = false //是否从头开始

    override fun processIntent(intent: FeedMatchIntent) {
        when (intent) {
            is FeedMatchIntent.ReqProfile -> getProfile()
            is FeedMatchIntent.ReqMatchOption -> matchOptions()
        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            val response = _profileRepository.getProfileInfo()
            val profile = response.data
            if (profile != null) {
                setState(FeedMatchState.ProfileResult(profile))
            }
        }
    }

    private fun matchOptions() {
        viewModelScope.launch {
            val response = _listRepository.getMatchOption()
            val list = response.data?.list
            if (!list.isNullOrEmpty()) {
                setState(FeedMatchState.MatchOptionResult(list))
            }
        }
    }

}