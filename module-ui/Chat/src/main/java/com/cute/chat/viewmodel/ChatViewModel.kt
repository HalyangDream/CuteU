package com.cute.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.chat.intent.ChatIntent
import com.cute.chat.state.ChatState
import com.cute.im.IMCore
import com.cute.im.bean.Msg
import com.cute.im.service.MessageService
import com.cute.logic.http.model.BehaviorRepository
import com.cute.logic.http.model.UserRepository
import kotlinx.coroutines.launch

class ChatViewModel : BaseMVIModel<ChatIntent, ChatState>() {

    private val _userRepository = UserRepository()
    private val _behaviorRepository = BehaviorRepository()

    private val _repository = IMCore.getService(MessageService::class.java)


    override fun processIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.MessageList -> getMessageList(
                intent.uid,
                intent.peerId,
                intent.isFirstLoad
            )

            is ChatIntent.MessageListForAnchor -> getMessageListForAnchor(intent.anchor)
            is ChatIntent.GetAnchorInfo -> getAnchorInfo(intent.peerId, intent.isFirst)
            is ChatIntent.SendTextMessage -> sendTextMessage(intent)
            is ChatIntent.SendImageMessage -> sendImageMessage(intent)
            is ChatIntent.SendVideoMessage -> sendVideoMessage(intent)
            is ChatIntent.BlockUser -> blockUser(intent.peerId)
            is ChatIntent.UnBlockUser -> unBlockUser(intent.peerId)
            is ChatIntent.ReportUser -> reportUser(intent.peerId, intent.reportType)
        }
    }


    private fun reportUser(peerId: Long, reportType: String) {
        viewModelScope.launch {
            val result = _behaviorRepository.reportUser(peerId, reportType)
            if (result.isSuccess) {
                setState(ChatState.ReportUserResult(true))
            } else {
                setState(ChatState.ReportUserResult(false))
            }
        }
    }


    private fun blockUser(peerId: Long) {
        viewModelScope.launch {
            val result = _behaviorRepository.blockUser(peerId)
            if (result.isSuccess) {
                setState(ChatState.BlockUserResult(true))
            } else {
                setState(ChatState.BlockUserResult(false))
            }
        }
    }

    private fun unBlockUser(peerId: Long) {
        viewModelScope.launch {
            val result = _behaviorRepository.unBlockUser(peerId)
            if (result.isSuccess) {
                setState(ChatState.UnBlockUserResult(true))
            } else {
                setState(ChatState.UnBlockUserResult(false))
            }
        }
    }


    private fun sendVideoMessage(intent: ChatIntent.SendVideoMessage) {
        MessageGlobalViewModel.sendVideoMessage(intent.uid, intent.peerId, intent.file) {
        }
    }

    private fun sendImageMessage(intent: ChatIntent.SendImageMessage) {
        MessageGlobalViewModel.sendImageMessage(intent.uid, intent.peerId, intent.file) {
        }
    }


    private fun sendTextMessage(intent: ChatIntent.SendTextMessage) {
        MessageGlobalViewModel.sendTextMessage(intent.uid, intent.peerId, intent.message) {
        }
    }

    private fun getAnchorInfo(anchorId: Long, isFirst: Boolean) {
        viewModelScope.launch {
            val userInfo = _userRepository.getChatUserInfo(anchorId).data
            setState(ChatState.AnchorInfo(isFirst, userInfo))
        }
    }


    private fun getMessageList(uid: String, peerId: String, isFirstLoad: Boolean) {
        viewModelScope.launch {
            val messageList = _repository.queryMessageList(20, uid, peerId)
            setState(ChatState.MessageListResult(messageList, isFirstLoad))
        }
    }

    private fun getMessageListForAnchor(message: Msg) {
        viewModelScope.launch {
            val messageList = _repository.queryMessageList(20, message)
            setState(ChatState.MessageListResult(messageList, false))
        }
    }
}