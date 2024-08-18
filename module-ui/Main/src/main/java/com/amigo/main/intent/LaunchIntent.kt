package com.amigo.main.intent

import com.amigo.basic.UserIntent

sealed class LaunchIntent : UserIntent {

    object  CheckToken : LaunchIntent()

    data class LoginWithVisitor(val token: String) : LaunchIntent()

}
