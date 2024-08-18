package com.amigo.home.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.user.UserDetail

sealed class AnchorDetailState : UserState {

    data class AnchorInfo(val anchorInfo: UserDetail?) : AnchorDetailState()

    data class FollowState(val isFollow: Boolean) : AnchorDetailState()

    data class BlockUserResult(val result:Boolean) : AnchorDetailState()

    data class UnBlockUserResult(val result:Boolean) : AnchorDetailState()

    data class ReportUserResult(val result:Boolean) : AnchorDetailState()
}
