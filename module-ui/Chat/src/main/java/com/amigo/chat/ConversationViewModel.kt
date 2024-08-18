package com.amigo.chat

import androidx.lifecycle.viewModelScope
import com.amigo.baselogic.storage.UserDataStore
import com.amigo.basic.BaseMVIModel
import com.amigo.chat.intent.ConversationIntent
import com.amigo.chat.state.ConversationState
import com.amigo.im.IMCore
import com.amigo.im.bean.Conversation
import com.amigo.im.service.ConversationService
import com.amigo.logic.http.model.ConfigRepository
import kotlinx.coroutines.launch

class ConversationViewModel : BaseMVIModel<ConversationIntent, ConversationState>() {


    private val _service by lazy { IMCore.getService(ConversationService::class.java) }
    private val _initRepository by lazy { ConfigRepository() }

    private val _officialConversation = mutableListOf<Conversation>()
    private val _loadNum: Long = 20


    override fun processIntent(intent: ConversationIntent) {
        when (intent) {

            is ConversationIntent.GetOfficialAccount -> getOfficialAccount(intent.userId)

            is ConversationIntent.LoadData -> loadConversation(intent.userId)

            is ConversationIntent.LoadMoreData -> loadMoreConversation(intent.anchor)
        }
    }


    private fun loadConversation(userId: String) {
        viewModelScope.launch {
            val data = _service.getConversation(_loadNum, "$userId")
            val isBottom = data.isNullOrEmpty() || data.size < _loadNum
            data?.removeAll(_officialConversation)
            setState(
                ConversationState.ConversationData(isBottom, data)
            )
        }
    }

    private fun getOfficialAccount(userId: String) {
        viewModelScope.launch {
            val officialAccount = _initRepository.getOfficialAccount().data?.official
            if (!officialAccount.isNullOrEmpty()) {
                val list =
                    _service.getConversationInPeerIds("$userId", mutableListOf(officialAccount))
                val conversation = if (list.isNullOrEmpty()) _service.generateConversation(
                    "$userId", officialAccount
                ) else list[0]
                _officialConversation.clear()
                _officialConversation.add(conversation)
                setState(ConversationState.HeaderData(_officialConversation, officialAccount))
            }
        }
    }

    private fun loadMoreConversation(anchor: Conversation) {
        viewModelScope.launch {
            val data = _service.getConversationAnchor(_loadNum, anchor)
            val isBottom = data.isNullOrEmpty() || data.size < _loadNum
            data?.removeAll(_officialConversation)
            setState(
                ConversationState.LoadMoreConversationData(isBottom, data)
            )
        }
    }
}