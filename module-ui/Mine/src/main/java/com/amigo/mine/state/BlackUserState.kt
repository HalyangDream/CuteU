package com.amigo.mine.state

import com.amigo.basic.ListState
import com.amigo.basic.UserState
import com.amigo.logic.http.response.list.BlackUser

sealed class BlackUserState : UserState {

    data class BlackListResult(val state: ListState<BlackUser>) : BlackUserState()

    data class RemoveBlackSuccess(val user: BlackUser): BlackUserState()
}
