package com.amigo.mine.intent

import com.amigo.basic.ListIntent
import com.amigo.basic.UserIntent
import com.amigo.logic.http.response.list.BlackUser
import com.amigo.mine.state.BlackUserState

sealed class BlackUserIntent : UserIntent {

    data class BlackList(val intent: ListIntent) : BlackUserIntent()

    data class RemoveBlack(val user: BlackUser): BlackUserIntent()

}
