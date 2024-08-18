package com.amigo.home.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.list.Feed
import com.amigo.logic.http.response.list.MatchOption
import com.amigo.logic.http.response.profile.Profile


sealed class FeedMatchState : UserState {


    data class ProfileResult(val profile: Profile) : FeedMatchState()
    data class MatchOptionResult(val list: MutableList<MatchOption>) : FeedMatchState()

}
