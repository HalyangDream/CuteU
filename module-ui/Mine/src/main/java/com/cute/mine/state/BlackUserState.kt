package com.cute.mine.state

import com.cute.basic.ListState
import com.cute.basic.UserState
import com.cute.logic.http.response.list.BlackUser

sealed class BlackUserState : UserState {

    data class BlackListResult(val state: ListState<BlackUser>) : BlackUserState()

    data class RemoveBlackSuccess(val user: BlackUser): BlackUserState()
}
