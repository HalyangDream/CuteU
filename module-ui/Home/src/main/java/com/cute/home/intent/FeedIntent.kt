package com.cute.home.intent

import com.cute.basic.ListIntent
import com.cute.basic.UserIntent

sealed class FeedIntent : UserIntent {
    data class List(val intent: ListIntent) : FeedIntent()

    data class Follow(val id: Long, val name: String) : FeedIntent()
}
