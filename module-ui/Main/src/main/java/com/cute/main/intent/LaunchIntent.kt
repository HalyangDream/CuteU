package com.cute.main.intent

import com.cute.basic.UserIntent

sealed class LaunchIntent : UserIntent {

    object  CheckToken : LaunchIntent()

    data class LoginWithVisitor(val token: String) : LaunchIntent()

}
