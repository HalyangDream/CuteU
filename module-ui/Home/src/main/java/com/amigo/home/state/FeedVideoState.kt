package com.amigo.home.state

import com.amigo.basic.ListState
import com.amigo.basic.UserState
import com.amigo.logic.http.response.list.VideoList

sealed class FeedVideoState : UserState {

    data class ListData(val state: ListState<VideoList>) : FeedVideoState()

}
