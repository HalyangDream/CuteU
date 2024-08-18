package com.amigo.home.intent

import com.amigo.basic.ListIntent
import com.amigo.basic.UserIntent

sealed class FeedVideoIntent : UserIntent {

    data class List(val intent: ListIntent) : FeedVideoIntent()

    data class Follow(val id: Long) : FeedVideoIntent()

    data class UnFollow(val id: Long) : FeedVideoIntent()
}
