package com.amigo.call.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.call.TelephoneService
import com.amigo.call.intent.CallIntent
import com.amigo.call.state.CallState
import com.amigo.im.IMCore
import com.amigo.im.service.ChatRoomService
import com.amigo.logic.http.model.BehaviorRepository
import com.amigo.logic.http.model.CallRepository
import com.amigo.logic.http.model.MessageRepository
import com.amigo.logic.http.model.UserRepository
import com.amigo.logic.http.response.call.CallInfoResponse
import com.amigo.logic.http.response.call.DeviceFunctionInfo
import com.amigo.message.custom.msg.TextMessage
import com.amigo.tool.EventBus
import com.amigo.uibase.event.FollowBehaviorEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CallViewModel : BaseMVIModel<CallIntent, CallState>() {


    private val _callRepository = CallRepository()
    private val _userRepository = UserRepository()
    private val _behaviorRepository = BehaviorRepository()


    private var _duration: Int = 0

    private val scheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private var future: ScheduledFuture<*>? = null

    init {
    }

    override fun onCleared() {
        super.onCleared()
        scheduledThreadPoolExecutor.shutdownNow()
    }

    override fun processIntent(intent: CallIntent) {
        when (intent) {
            is CallIntent.GetAnchorInfo -> getAnchorInfo(intent.anchorId)

            is CallIntent.RequestDeviceFunction -> getUnlockDeviceFunctionInfo()

            is CallIntent.UnlockDeviceFunction -> unlockDeviceFunction(intent.info)

            is CallIntent.Like -> like(intent.anchorId)
            is CallIntent.UnLike -> unlike(intent.anchorId)

            is CallIntent.StartCall -> {
                _duration = 0
                viewModelScope.launch {
                    setState(CallState.StartCall(intent.callId, intent.anchorId.toInt()))
                    launchCallCountDown(intent.callId)
                }
            }

            is CallIntent.CallInfo -> getCallInfo(intent.callId)

            is CallIntent.FinishCall -> finishCall(intent.callId, intent.reason)

        }
    }


    private fun getAnchorInfo(anchorId: Long) {
        viewModelScope.launch {
            val userInfo = _userRepository.getChatUserInfo(anchorId).data
            if (userInfo != null) {
                setState(CallState.UserInfoResult(userInfo))
                setState(CallState.FollowState(userInfo.isFollow))
            }
        }
    }

    private fun like(anchorId: Long) {
        viewModelScope.launch {
            val response = _behaviorRepository.followerUser(anchorId)
            if (response.isSuccess) {
                setState(CallState.FollowState(true))
                EventBus.post(FollowBehaviorEvent.Follow(anchorId))
            }
        }
    }

    private fun unlike(anchorId: Long) {
        viewModelScope.launch {
            val response = _behaviorRepository.unFollowerUser(anchorId)
            if (response.isSuccess) {
                setState(CallState.FollowState(false))
                EventBus.post(FollowBehaviorEvent.UnFollow(anchorId))
            }
        }
    }


    private fun launchCallCountDown(channel: String) {
        _duration = 0
        future = scheduledThreadPoolExecutor.scheduleAtFixedRate({
            val isOnCall = TelephoneService.stateIsCalling()
            if (!isOnCall) {
                future?.cancel(true)
                return@scheduleAtFixedRate
            }
            viewModelScope.launch {
                setState(CallState.UpdateCallDuration(_duration))
            }
            if (_duration == 0 || _duration % 10 == 0) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        heart(channel)
                    }
                }
            }
            _duration += 1
        }, 1, 1, TimeUnit.SECONDS)
    }

    private fun getCallInfo(callId: String) {
        viewModelScope.launch {
            val response = _callRepository.getCallInfo(callId)
            val data = response.data
            if (data != null) {
                setState(CallState.CallInfoResult(data))
            }
        }
    }

    private suspend fun heart(callId: String) {
        val response = _callRepository.heartCall(callId)
        if (!response.isSuccess || response.data?.isContinue == false) {
            setState(CallState.HeartFinish(callId))
        }
    }


    private fun finishCall(callId: String, reason: String) {
        viewModelScope.launch {
            val response = _callRepository.finishCall(callId, reason)
            if (response.isSuccess) {
                setState(CallState.FinishCallSuccess(callId, reason))
            } else {
                setState(CallState.FinishCallFailure(callId, response.msg))
            }
        }
    }


    private fun sendTextMessage() {
//        val textMessage = TextMessage()
//        textMessage.messageContent = intent.message
//        IMCore.getService(ChatRoomService::class.java).sendMessage(
//            channel = intent.channel, customMessage = textMessage
//        ) { code, msg ->
//            msg?.let {
//                viewModelScope.launch {
//                }
//            }
//        }
    }

    private fun getUnlockDeviceFunctionInfo() {
        viewModelScope.launch {
            val response = _callRepository.deviceFunctionUnlockInfo()
            val list = response.data?.list
            val cameraClose = list?.filter { it.type == "camera_close" }?.get(0)
            val cameraSwitch = list?.filter { it.type == "camera_switch" }?.get(0)
            val voiceMute = list?.filter { it.type == "voice_mute" }?.get(0)
            setState(
                CallState.DeviceFunctionInfoResult(
                    cameraClose, cameraSwitch, voiceMute
                )
            )
        }
    }

    private fun unlockDeviceFunction(info: DeviceFunctionInfo) {
        viewModelScope.launch {
            val response = _callRepository.unlockDeviceFunction(info.id)
            setState(CallState.UnlockDeviceFunctionResult(response.isSuccess, info))
        }
    }
}