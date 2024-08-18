package com.amigo.home.intent

import com.amigo.basic.UserIntent


sealed class FeedMatchIntent : UserIntent {
    object ReqProfile : FeedMatchIntent()

    object ReqMatchOption : FeedMatchIntent()

}
