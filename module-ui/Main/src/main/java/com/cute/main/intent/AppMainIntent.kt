package com.cute.main.intent

import com.cute.basic.UserIntent

sealed class AppMainIntent : UserIntent {

    object InitService : AppMainIntent()
    
    data class GetUnReadCount(val uid: Long) : AppMainIntent()

    object NewUserProductInfo : AppMainIntent()
}
