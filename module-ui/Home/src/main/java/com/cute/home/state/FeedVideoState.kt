package com.cute.home.state

import com.cute.basic.ListState
import com.cute.basic.UserState
import com.cute.logic.http.response.list.VideoList

sealed class FeedVideoState : UserState {

    data class ListData(val state: ListState<VideoList>) : FeedVideoState()

}
