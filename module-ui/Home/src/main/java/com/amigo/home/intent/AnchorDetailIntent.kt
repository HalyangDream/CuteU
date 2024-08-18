package com.amigo.home.intent

import com.amigo.basic.UserIntent

sealed class AnchorDetailIntent : UserIntent {

    data class GetAnchorInfo(val anchorId: Long) : AnchorDetailIntent()

    data class Follow(val anchorId: Long) : AnchorDetailIntent()

    data class UnFollow(val anchorId: Long) : AnchorDetailIntent()

    data class BlockUser(val peerId: Long) : AnchorDetailIntent()

    data class UnBlockUser(val peerId: Long) : AnchorDetailIntent()

    data class ReportUser(val peerId: Long, val reportType: String) : AnchorDetailIntent()
}
