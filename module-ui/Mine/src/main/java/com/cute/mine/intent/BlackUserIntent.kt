package com.cute.mine.intent

import com.cute.basic.ListIntent
import com.cute.basic.UserIntent
import com.cute.logic.http.response.list.BlackUser
import com.cute.mine.state.BlackUserState

sealed class BlackUserIntent : UserIntent {

    data class BlackList(val intent: ListIntent) : BlackUserIntent()

    data class RemoveBlack(val user: BlackUser): BlackUserIntent()

}
