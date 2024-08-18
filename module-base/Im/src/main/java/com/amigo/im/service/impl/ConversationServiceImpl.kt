package com.amigo.im.service.impl

import androidx.core.text.isDigitsOnly
import com.amigo.im.DbManager
import com.amigo.im.MessageObserver
import com.amigo.im.bean.Conversation
import com.amigo.im.bean.Msg
import com.amigo.im.dao.ConversationDao
import com.amigo.im.service.ConversationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * author : mac
 * date   : 2022/5/11
 *
 */
class ConversationServiceImpl : ConversationService {


    private var chatPeerId: String? = ""

    private val dao by lazy { DbManager.get().getDao(ConversationDao::class.java) }

    private fun insertAndUpdate(conversation: Conversation) {
        val row = dao.update(conversation)
        if (row != 1) {
            dao.insert(conversation)
        }
    }

    /**
     * 生成聊天对话频道id
     *
     * @param peerId
     * @return
     */
    override fun getChannelId(uid: String, peerId: String): String {
        if (!uid.isDigitsOnly() || !peerId.isDigitsOnly()) return ""
        return if (uid.toInt() < peerId.toInt()) "$uid$peerId" else "$peerId$uid"
    }

    override fun generateConversation(userId: String, peerId: String): Conversation {
        return Conversation(
            channel = getChannelId(userId, peerId),
            uid = userId,
            peer = peerId,
            messageType = 1,
            unreadCount = 0
        )
    }


    override suspend fun addConversation(conversation: Conversation) {
        insertAndUpdate(conversation)
    }


    override suspend fun addOrUpdateConversation(
        uid: String,
        peerId: String,
        msg: Msg,
        notify: Boolean
    ) {
        val conversation = dao.query(msg.channel) ?: generateConversation(uid, peerId)
        conversation.unreadCount += 1
        conversation.messageType = msg.mark
        conversation.lastMessage = msg.message?.shortContent()
        conversation.timeStamp = msg.timeStamp
        insertAndUpdate(conversation)
        if (conversation != null && notify) {
            MessageObserver.notifyConversationListener(conversation)
        }
    }

    override suspend fun updateConversation(
        uid: String,
        peerId: String,
        msg: Msg,
        notify: Boolean
    ) {
        val conversation = dao.query(msg.channel) ?: generateConversation(uid, peerId)
        conversation.messageType = msg.mark
        conversation.lastMessage = msg.message?.shortContent()
        conversation.timeStamp = msg.timeStamp
        insertAndUpdate(conversation)
        if (conversation != null && notify) {
            MessageObserver.notifyConversationListener(conversation)
        }
    }

    override suspend fun getAllConversation(userId: String): List<Conversation>? {
        return withContext(Dispatchers.IO) { dao.queryBySelf(self = userId) }
    }


    override suspend fun getConversation(channel: String): Conversation? {
        return withContext(Dispatchers.IO) { dao.query(channel) }
    }

    override suspend fun getConversation(uid: String, peerId: String): Conversation? {
        return withContext(Dispatchers.IO) {
            val channel = getChannelId(uid, peerId)
            dao.query(channel)
        }
    }

    override suspend fun getConversationAnchor(
        count: Long,
        anchor: Conversation
    ): MutableList<Conversation>? {
        return withContext(Dispatchers.IO) {
            dao.queryByAnchorCondition(
                count,
                anchor.uid,
                anchor.channel,
                anchor.timeStamp
            )
        }
    }

    override suspend fun getConversation(count: Long, userId: String): MutableList<Conversation>? {

        return withContext(Dispatchers.IO) { dao.queryBySelf(count, userId) }
    }


    override suspend fun getConversationInChannel(channels: Array<String>): List<Conversation>? {
        return withContext(Dispatchers.IO) { dao.queryInChannel(channels.toList()) }
    }

    override suspend fun getConversationInPeerIds(
        userId: String,
        peerId: Collection<String>
    ): List<Conversation>? {
        return withContext(Dispatchers.IO) { dao.queryInPeer(userId, peerId.toList()) }
    }

    override suspend fun getUnReadCount(uid: String): Int {
        return withContext(Dispatchers.IO) {
            dao.queryUnReadCount(uid)
        }
    }

    override suspend fun updateConversation(conversation: Conversation) {
        insertAndUpdate(conversation)
    }

    override suspend fun deleteConversation(channel: String) {
        val conversation = Conversation(channel = channel, uid = "", peer = "", messageType = 1)
        dao.deleteByChannel(channel)
        MessageObserver.notifyDeletedConversationListener(mutableListOf(conversation))
    }

    override suspend fun deleteConversation(userId: String, peerId: String) {
        val conversation = Conversation(
            channel = getChannelId(userId, peerId),
            uid = userId,
            peer = peerId,
            messageType = 1
        )
        dao.deleteByChannel(getChannelId(userId, peerId))
        MessageObserver.notifyDeletedConversationListener(mutableListOf(conversation))
    }

    override suspend fun deleteConversations(list: MutableList<Conversation>) {
        for (conversation in list) {
            dao.delete(conversation)
        }
        MessageObserver.notifyDeletedConversationListener(list)
    }

    override suspend fun clearAllUnReadCount(uid: String) {
        dao.clearUnReadCountForUid(uid)
    }

    override suspend fun clearUnReadCountByPeer(uid: String, peerId: String) {
        val channel = getChannelId(uid, peerId)
        dao.clearUnReadCountForChannel(channel)
        val conversation = getConversation(channel)
        if (conversation != null) {
            MessageObserver.notifyConversationListener(conversation)
        }
    }


//    override fun clearUnReadCountByPeer(peerId: String?) {
//        val userId = IMCore.getService(UserService::class.java).getLoginUserId()
//        val channel = MessageUtils.getChannelId(userId, peerId)
//        if (channel.isNullOrEmpty()) return
//        ConversationDbManager.get().clearUnReadCount(channel)
//        val conversation = getConversation(channel)
//        MessageObserver.notifyConversationListener(conversation)
//    }

//    override fun clearAllUnReadCount() {
//        val userId = IMCore.getService(UserService::class.java).getLoginUserId()
//        if (userId.isNullOrEmpty()) return
//        ConversationDbManager.get().clearAllUnReadCount(userId)
//        ThreadPool.runOnBackgroundThread {
//            val list = ConversationDbManager.get().queryAllConversation(userId)
//            ThreadPool.runOnUiThread {
//                if (!list.isNullOrEmpty()) {
//                    for (conversation in list) {
//                        MessageObserver.notifyConversationListener(conversation)
//                    }
//                }
//            }
//        }
//    }

    override fun isChat(peerId: String): Boolean {
        if (peerId.isNullOrEmpty()) return false
        if (this.chatPeerId.isNullOrEmpty()) return false
        return this.chatPeerId.equals(peerId, false)
    }


    override fun setChatUser(peerId: String) {
        this.chatPeerId = peerId
    }

    override fun getChattingId(): String? {
        return chatPeerId
    }

    override fun removeChatUser(peerId: String) {
        if (this.chatPeerId.equals(peerId)) {
            this.chatPeerId = null
        }
    }
}