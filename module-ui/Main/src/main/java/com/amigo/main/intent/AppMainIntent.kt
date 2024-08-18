package com.amigo.main.intent

import com.amigo.basic.UserIntent

sealed class AppMainIntent : UserIntent {

    object InitService : AppMainIntent()
    
    data class GetUnReadCount(val uid: Long) : AppMainIntent()

    object NewUserProductInfo : AppMainIntent()
}
