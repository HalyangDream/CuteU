package com.cute.home.state

import com.cute.basic.UserState
import com.cute.logic.http.response.user.UserDetail

sealed class AnchorDetailState : UserState {

    data class AnchorInfo(val anchorInfo: UserDetail?) : AnchorDetailState()

    data class FollowState(val isFollow: Boolean) : AnchorDetailState()

    data class BlockUserResult(val result:Boolean) : AnchorDetailState()

    data class UnBlockUserResult(val result:Boolean) : AnchorDetailState()

    data class ReportUserResult(val result:Boolean) : AnchorDetailState()
}
