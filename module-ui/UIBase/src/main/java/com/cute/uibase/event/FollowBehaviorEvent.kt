package com.cute.uibase.event

sealed class FollowBehaviorEvent {

    data class Follow(val id: Long) : FollowBehaviorEvent()

    data class UnFollow(val id: Long) : FollowBehaviorEvent()
}
