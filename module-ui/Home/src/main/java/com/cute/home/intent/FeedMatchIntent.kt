package com.cute.home.intent

import com.cute.basic.UserIntent


sealed class FeedMatchIntent : UserIntent {
    object ReqProfile : FeedMatchIntent()

    object ReqMatchOption : FeedMatchIntent()

}
