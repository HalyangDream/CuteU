package com.amigo.call

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.amigo.call.intent.TelephoneIntent
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.provider.ITelephoneService

@Route(path = RoutePage.Provider.TELEPHONE_PROVIDER)
class ITelephoneServiceImpl : ITelephoneService {
    override fun init(context: Context?) {
    }

    override fun init() {
        TelephoneService.initTelephoneService()
    }

    override fun callServiceIsEnable(): Boolean {
        return TelephoneService.callServerEnable()
    }


    override fun isCalling(): Boolean = TelephoneService.isCalling()

    override fun sendCallInvited(context: Context, caller: Long, callee: Long, source: String) {
        TelephoneService.processIntent(TelephoneIntent.LaunchCall(context, caller, callee, source))
    }

    override fun launchStrategyCall(
        caller: Long,
        callee: Long,
        isFreeCall: Boolean,
        source: String
    ) {
        TelephoneService.processIntent(
            TelephoneIntent.LaunchStrategyCall(
                caller,
                callee,
                isFreeCall,
                source
            )
        )
    }

    override fun launchMatch(caller:Long,matchId: Long, source: String) {
        TelephoneService.processIntent(
            TelephoneIntent.LaunchMatchCall(
                caller,matchId, source
            )
        )
    }



    override fun tryResumeCall() {
        TelephoneService.processIntent(TelephoneIntent.TryResumeCall)
    }

}