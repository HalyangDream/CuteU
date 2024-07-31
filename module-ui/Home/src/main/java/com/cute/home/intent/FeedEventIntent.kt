package com.cute.home.intent

import com.cute.basic.ListIntent
import com.cute.basic.UserIntent

sealed class FeedEventIntent : UserIntent {


    data class Loading(val source: String) : FeedEventIntent()
    data class List(val intent: ListIntent) : FeedEventIntent()
}
