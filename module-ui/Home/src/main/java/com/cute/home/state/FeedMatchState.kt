package com.cute.home.state

import com.cute.basic.UserState
import com.cute.logic.http.response.list.Feed
import com.cute.logic.http.response.list.MatchOption
import com.cute.logic.http.response.profile.Profile


sealed class FeedMatchState : UserState {


    data class ProfileResult(val profile: Profile) : FeedMatchState()
    data class MatchOptionResult(val list: MutableList<MatchOption>) : FeedMatchState()

}
