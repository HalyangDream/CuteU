package com.cute.home.state

import com.cute.basic.ListState
import com.cute.basic.UserState

sealed class FeedState : UserState {

    data class ListData(val state: ListState<*>) : FeedState()

}
