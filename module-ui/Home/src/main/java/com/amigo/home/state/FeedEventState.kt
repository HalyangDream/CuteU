package com.amigo.home.state

import com.amigo.basic.ListState
import com.amigo.basic.UserState

sealed class FeedEventState : UserState {
    data class ListData(val state: ListState<*>) : FeedEventState()
}
