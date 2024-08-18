package com.amigo.home.intent

import com.amigo.basic.ListIntent
import com.amigo.basic.UserIntent

sealed class FeedIntent : UserIntent {
    data class List(val intent: ListIntent) : FeedIntent()

    data class Follow(val id: Long, val name: String) : FeedIntent()
}
