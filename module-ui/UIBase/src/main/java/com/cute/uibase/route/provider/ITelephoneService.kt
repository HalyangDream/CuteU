package com.cute.uibase.route.provider

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface ITelephoneService : IProvider {


    fun init()

    fun callServiceIsEnable(): Boolean

    fun isCalling(): Boolean

    fun sendCallInvited(context: Context, caller: Long, callee: Long, source: String)

    fun launchStrategyCall(caller: Long, callee: Long, isFreeCall: Boolean, source: String)

    fun launchMatch(caller:Long,matchId: Long, source: String)

    fun tryResumeCall()
}