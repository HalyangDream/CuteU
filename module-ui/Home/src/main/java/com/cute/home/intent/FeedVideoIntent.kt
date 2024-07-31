package com.cute.home.intent

import com.cute.basic.ListIntent
import com.cute.basic.UserIntent

sealed class FeedVideoIntent : UserIntent {

    data class List(val intent: ListIntent) : FeedVideoIntent()

    data class Follow(val id: Long) : FeedVideoIntent()

    data class UnFollow(val id: Long) : FeedVideoIntent()
}
