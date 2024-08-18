package com.amigo.home.intent

import com.amigo.basic.ListIntent
import com.amigo.basic.UserIntent

sealed class FeedEventIntent : UserIntent {


    data class Loading(val source: String) : FeedEventIntent()
    data class List(val intent: ListIntent) : FeedEventIntent()
}
