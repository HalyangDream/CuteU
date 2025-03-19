package com.amigo.call

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.amigo.analysis.Analysis
import com.amigo.basic.UserState
import com.amigo.call.intent.TelephoneIntent
import com.amigo.call.state.TelephoneCallerState
import com.amigo.im.IMCore
import com.amigo.im.service.MessageService
import com.amigo.logic.http.model.CallRepository
import com.amigo.message.custom.msg.CallRecordMessage
import com.amigo.tool.Toaster
import com.amigo.uibase.ActivityStack
import com.amigo.uibase.userbehavior.UserBehavior
import com.amigo.uibase.media.RingPlayer
import com.amigo.uibase.route.RouteSdk
import io.agora.rtm.RemoteInvitation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object TelephoneService {

    /**
     * 通话状态
     */
    private const val NO_INIT = -1
    private const val PREPARE = 0
    private const val MAKE_CALL = 1
    private const val CALLING = 2

    private val _callRepository = CallRepository()
    private val _callScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var telephoneState = NO_INIT

    private val _telephoneStateFlow =
        MutableSharedFlow<UserState>(replay = 0, extraBufferCapacity = 100)
    private var bundle: Bundle? = null
    private var delayJobQueue = mutableListOf<Job?>()

    val telephoneStateFlow: SharedFlow<UserState> = _telephoneStateFlow
    private var isMatchCall = false


    fun isCalling(): Boolean = telephoneState > PREPARE

    fun stateIsMakeCall(): Boolean = telephoneState == MAKE_CALL

    fun stateIsCalling(): Boolean = telephoneState == CALLING

    fun callServerEnable(): Boolean = telephoneState > NO_INIT

    fun initTelephoneService() {
        telephoneState = PREPARE
    }


    fun processIntent(intent: TelephoneIntent) {
        if (!callServerEnable()) {
            initTelephoneService()
        }

        when (intent) {
            is TelephoneIntent.LaunchCall -> launchCall(
                context = intent.context, intent.caller, intent.callee, intent.source
            )

            is TelephoneIntent.LaunchStrategyCall -> launchStrategyCall(
                intent.caller, intent.callee, intent.isFreeCall, intent.source
            )

            is TelephoneIntent.LaunchMatchCall -> launchMatchCall(
                intent.caller, intent.matchId, intent.source
            )

            is TelephoneIntent.TryResumeCall -> tryResumeCall()


            is TelephoneIntent.MakeCall -> invitedPrepare(
                caller = intent.caller, callee = intent.callee, matchId = 0, source = intent.source
            )

            is TelephoneIntent.MakeMatchCall -> invitedPrepare(
                caller = intent.caller, callee = 0, matchId = intent.matchId, source = intent.source
            )

            is TelephoneIntent.CancelCall -> {
                if (intent.callId.isNullOrEmpty()) {
                    setState(TelephoneCallerState.OperateCancelInvitedSuccess)
                } else {
                    cancelInvited(intent.callId)
                }
            }

            is TelephoneIntent.ProcessCall -> {
                if (telephoneState == MAKE_CALL) {
                    stopDelayJob()
                    telephoneState = CALLING
                }
            }

            is TelephoneIntent.StopRing -> RingPlayer.stopRinging()

            is TelephoneIntent.FinishCommunication -> {
                stopDelayJob()
                finishCommunication(intent.reason)
            }

            is TelephoneIntent.FinishCall -> {
                stopDelayJob()
                finishCall(intent.callId, intent.reason)
            }
        }
    }


    private fun setState(state: UserState) {
        _callScope.launch {
            _telephoneStateFlow.emit(state)
        }
    }

    private fun launchCall(context: Context, caller: Long, callee: Long, source: String) {
        if (telephoneState == PREPARE) {
            isMatchCall = false
            telephoneState = MAKE_CALL
            //启动拨打页面
            val launchBundle = Bundle()
            launchBundle.putLong("caller", caller)
            launchBundle.putLong("callee", callee)
            launchBundle.putString("source", source)
            launchBundle.putBoolean("isMatchCall", false)
            launchBundle.putBoolean("isStrategyCall", false)
            CallActivity.startLaunchCall(context, launchBundle)
            RingPlayer.startRinging(ActivityStack.application, R.raw.call_ring)
            bundle = launchBundle
            Analysis.track("video_call_click", mutableMapOf<String, Any>().apply {
                put("anchor_id", "$callee")
                put("source", source)
            })

        } else {
            if (telephoneState > PREPARE) {
                Toaster.showShort(context, com.amigo.uibase.R.string.str_in_call)
                Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                    put("anchor_id", "$callee")
                    put("source", source)
                    put("reason", "正在通话")
                })

            } else {
                Toaster.showShort(context, com.amigo.uibase.R.string.str_call_server_unable)
                initTelephoneService()
                Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                    put("anchor_id", "$callee")
                    put("source", source)
                    put("reason", "功能未启用")
                })

            }
        }
    }


    /**
     * 收到策略呼叫
     */
    private fun launchStrategyCall(
        caller: Long, callee: Long, isFreeCall: Boolean, source: String
    ) {
        if (telephoneState != PREPARE) {
            //正在通话中
            return
        }
        isMatchCall = false
        telephoneState = MAKE_CALL
        //启动拨打页面
        val launchBundle = Bundle()
        launchBundle.putLong("caller", caller)
        launchBundle.putLong("callee", callee)
        launchBundle.putString("source", source)
        launchBundle.putBoolean("isMatchCall", false)
        launchBundle.putBoolean("isStrategyCall", true)
        CallActivity.startLaunchCall(ActivityStack.application, launchBundle)
        RingPlayer.startRinging(ActivityStack.application, R.raw.call_ring)
        UserBehavior.setRootPage("incoming_call")
        UserBehavior.setChargeSource("incoming_call")
        bundle = launchBundle
    }

    /**
     * 启动匹配主播功能
     */
    private fun launchMatchCall(
        caller: Long, matchId: Long, source: String
    ) {
        if (telephoneState != PREPARE) {
            //正在通话中
            return
        }
        isMatchCall = true
        telephoneState = MAKE_CALL
        //启动拨打页面
        val launchBundle = Bundle()
        launchBundle.putLong("caller", caller)
        launchBundle.putLong("matchId", matchId)
        launchBundle.putString("source", source)
        launchBundle.putBoolean("isMatchCall", true)
        launchBundle.putBoolean("isStrategyCall", false)
        bundle = launchBundle
        CallActivity.startLaunchCall(ActivityStack.application, launchBundle)
        Analysis.track("video_call_click", mutableMapOf<String, Any>().apply {
            put("anchor_id", "$matchId")
            put("source", source)
        })

    }

    /**
     * 尝试恢复通话的接听页面
     */
    private fun tryResumeCall() {
        try {
            if (bundle == null) return

            if (telephoneState != MAKE_CALL) {
                bundle = null
                return
            }
            val activity = ActivityStack.getTopActivity()
            if (activity != null && activity is CallActivity) {
                bundle = null
                return
            }
            CallActivity.startLaunchCall(ActivityStack.application, bundle!!)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private fun invitedPrepare(
        caller: Long,
        callee: Long,
        matchId: Long,
        source: String
    ) {
        _callScope.launch {
            val popResponse = _callRepository.showPop()
            val popData = popResponse.data
            if (!popResponse.isSuccess || popData == null) {
                sendInvited(caller, callee, matchId, source)
            } else {
                val popCode = popData.popCode
                if (!popData.isShow) {
                    sendInvited(caller, callee, matchId, source)
                    return@launch
                }
                val dialog = RouteSdk.handleResponseDialogCode(popCode, null)
                if (dialog != null) {
                    dialog.setDialogDismissListener {
                        sendInvited(caller, callee, matchId, source)
                    }
                } else {
                    sendInvited(caller, callee, matchId, source)
                }
            }
        }
    }


    private fun sendInvited(caller: Long, callee: Long, matchId: Long, source: String) {
        _callScope.launch {
            val response = if (isMatchCall) {
                _callRepository.matchInvitedCall(matchId, source, null)
            } else {
                _callRepository.invitedCall(callee, source, Bundle().apply {
                    putLong("anchorId", callee)
                })
            }
            if (response.isSuccess) {
                val data = response.data!!
                val connectTime = data.intoConnectStateDuration
                val loadVideoTime = data.loadVideoWaitDuration
                val videoUrl = data.videoUrl
                val videoEndTime = data.playEndTime
                val job1 = _callScope.launch {
                    delay(connectTime * 1000L)
                    //执行链接中的状态
                    setState(TelephoneCallerState.Connecting(data.isFree))
                    //执行预加载视频
                    setState(TelephoneCallerState.StartLoadVideo(videoUrl, videoEndTime))
                    //执行加载视频
                    setState(TelephoneCallerState.CanPlayVideo(true))
                    //执行等待加载视频的事件，时间到达没播放就挂断
                    delay(loadVideoTime * 1000L)
                    //执行完,没有加载中的理应结束
                    if (telephoneState == MAKE_CALL) {
                        setState(TelephoneCallerState.CanPlayVideo(false))
                        setState(TelephoneCallerState.OperateCalleeOffline)
                        Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                            put("anchor_id", "$callee")
                            put("source", source)
                            put("reason", "视频加载失败")
                        })

                    }
                }
                delayJobQueue.add(job1)
                setState(
                    TelephoneCallerState.OperateInvitedSuccess(
                        caller,
                        data.remoteId,
                        data.callId
                    )
                )

            } else {
                when (response.code) {
                    "6015" -> {
                        setState(TelephoneCallerState.OperateCalleeBusy)
                        if (!isMatchCall) {
                            insertCallRecord(
                                caller, callee, com.amigo.uibase.R.string.str_call_buys, 0
                            )
                        }
                        Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                            put("anchor_id", "$callee")
                            put("source", source)
                            put("reason", "用户Busy")
                        })

                    }

                    "80002" -> {
                        setState(TelephoneCallerState.OperateCalleeOffline)
                        if (!isMatchCall) {
                            insertCallRecord(
                                caller, callee, com.amigo.uibase.R.string.str_call_offline, 0
                            )
                        }
                        Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                            put("anchor_id", "$callee")
                            put("source", source)
                            put("reason", "用户离线")
                        })

                    }

                    else -> {
                        setState(
                            TelephoneCallerState.OperateInvitedFailure(response.code, response.msg)
                        )
                        if (!isMatchCall) {
                            insertCallRecord(
                                caller, callee, com.amigo.uibase.R.string.str_call_failure, 0
                            )
                        }
                        Analysis.track("video_call_err", mutableMapOf<String, Any>().apply {
                            put("anchor_id", "$callee")
                            put("source", source)
                            put("reason", "code:${response.code}")
                        })

                    }
                }
            }
        }
    }

    private fun cancelInvited(callId: String) {
        _callScope.launch {
            val response = _callRepository.cancelCall(callId)
            if (response.isSuccess) {
                setState(TelephoneCallerState.OperateCancelInvitedSuccess)
            } else {
                setState(
                    TelephoneCallerState.OperateCancelInvitedFailure(response.code, response.msg)
                )
            }
        }
    }


    private fun stopDelayJob() {
        for (job in delayJobQueue) {
            job?.cancel()
        }
        delayJobQueue.clear()
    }


    private fun finishCommunication(reason: String) {
        Log.i("TelephoneService", "finishCommunication reason:$reason")
        if (telephoneState == MAKE_CALL) {
            setState(TelephoneCallerState.FinishCommunication(reason))
            telephoneState = PREPARE
            bundle = null
            RingPlayer.stopRinging()
        }
    }

    private fun finishCall(channel: String, reason: String) {
        Log.i("TelephoneService", "finishCall reason:$reason")
        if (telephoneState != CALLING || channel.isEmpty()) return
        setState(TelephoneCallerState.FinishCall(channel, reason))
        telephoneState = PREPARE
        bundle = null
    }

    private fun insertCallRecord(caller: Long, callee: Long, resId: Int, duration: Long) {
        val callRecordMessage = CallRecordMessage()
        callRecordMessage.message_content = ActivityStack.application.getString(resId)
        callRecordMessage.duration = duration
        _callScope.launch {
            IMCore.getService(MessageService::class.java)
                .insertLocalMessage("$caller", "$callee", callRecordMessage, true)
        }
    }
}