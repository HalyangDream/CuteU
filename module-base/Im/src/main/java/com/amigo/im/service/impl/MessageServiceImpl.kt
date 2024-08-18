package com.amigo.im.service.impl

import androidx.core.text.isDigitsOnly
import com.amigo.im.DbManager
import com.amigo.im.IMCore
import com.amigo.im.MessageObserver
import com.amigo.im.MessageUtils
import com.amigo.im.bean.MessageStatus
import com.amigo.im.bean.Msg
import com.amigo.im.cutom.CustomMessage
import com.amigo.im.dao.MsgDao
import com.amigo.im.rtm.RtmManager
import com.amigo.im.service.ConversationService
import com.amigo.im.service.MessageService
import com.amigo.im.service.UserService
import io.agora.rtm.SendMessageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * author : mac
 * date   : 2022/5/12
 *
 */
class MessageServiceImpl : MessageService {


    private val msgDao by lazy { DbManager.get().getDao(MsgDao::class.java) }


    private fun insertOrReplace(msg: Msg) {
        val row = msgDao.update(msg)
        if (row == 0) {
            msgDao.insert(msg)
        }
    }

    /**
     * 生成messageId
     */
    private fun generateMessageId(): String {
        return UUID.randomUUID().toString()
    }

    private fun getChannelId(senderId: String, peerId: String): String {
        if (!senderId.isDigitsOnly() || !peerId.isDigitsOnly()) return ""

        return if (senderId.toInt() < peerId.toInt()) "$senderId$peerId" else "$peerId$senderId"
    }

    override fun generateIMMessage(
        senderId: String,
        peerId: String,
        customMessage: CustomMessage
    ): Msg {

        val imMessage =
            Msg(
                messageId = generateMessageId(),
                channel = getChannelId(senderId, peerId),
                sendId = senderId,
                receiveId = peerId,
                timeStamp = System.currentTimeMillis(),
                mark = customMessage.identity(),
                type = "message",
                status = MessageStatus.SENDING
            ).apply {
                message = customMessage
            }
        imMessage.originJson = imMessage.toJson()
        return imMessage
    }

    override suspend fun sendMessage(peerId: String, message: CustomMessage) {
        val userId = IMCore.getService(UserService::class.java).getLoginUserId()
        if (userId.isNullOrEmpty()) return
        val channel =
            IMCore.getService(ConversationService::class.java).getChannelId(userId, peerId)
        val rtmMsg = RtmManager.getInstance().createRtmMessage()
        val imMessage = generateIMMessage(userId, peerId, message)
        rtmMsg?.text = imMessage.toJson()
        val options = SendMessageOptions()
//        options.enableHistoricalMessaging = true
//        options.enableOfflineMessaging = true
        imMessage.status = MessageStatus.SENDING
        insertMessage(imMessage)
        RtmManager.getInstance().sendRtmMessage(channel, rtmMsg) {
            imMessage.status = if (it == 0) MessageStatus.SUCCESS else MessageStatus.FAIL
            runBlocking {
                MessageObserver.notifySendResultListener(it, imMessage.status.name, imMessage)
                updateMessage(imMessage, false)
                IMCore.getService(ConversationService::class.java)
                    .addOrUpdateConversation(userId, peerId, imMessage, true)
            }
        }
    }


    override suspend fun insertMessage(msg: Msg, notify: Boolean) {
        val loginUserId = IMCore.getService(UserService::class.java).getLoginUserId()
        if (!loginUserId.isNullOrEmpty()) {
            val conversationService = IMCore.getService(ConversationService::class.java)
            val uid = if (loginUserId == msg.sendId) loginUserId else msg.receiveId
            val peerId = if (loginUserId == msg.sendId) msg.receiveId else msg.sendId
            conversationService.addOrUpdateConversation(uid, peerId, msg, notify)
            msg.originJson = msg.toJson()
            msg.message =
                MessageUtils.parseCustomMsgToIMMessage(msg.originJson!!, msg.mark)
            insertOrReplace(msg)
            if (notify) {
                MessageObserver.notifyMsgListener(msg)
            }
        }
    }

    override suspend fun insertLocalMessage(
        senderId: String,
        peerId: String,
        message: CustomMessage,
        notify: Boolean
    ) {
        val imMessage = generateIMMessage(senderId, peerId, message)
        imMessage.status = MessageStatus.SUCCESS
        imMessage.message = MessageUtils.parseCustomMsgToIMMessage(
            imMessage.originJson!!,
            imMessage.mark
        )
        imMessage.toJson()
        insertOrReplace(imMessage)
        if (notify) {
            MessageObserver.notifyMsgListener(imMessage)
        }
        val loginUserId = IMCore.getService(UserService::class.java).getLoginUserId()
        if (!loginUserId.isNullOrEmpty()) {
            val uid = if (loginUserId == senderId) senderId else peerId
            val oppoSite = if (loginUserId == senderId) peerId else senderId
            IMCore.getService(ConversationService::class.java)
                .updateConversation(uid, oppoSite, imMessage, notify)
        }
    }

    override suspend fun insertLocalMessage(message: Msg, notify: Boolean) {
        message.toJson()
        insertOrReplace(message)
        if (notify) {
            MessageObserver.notifyMsgListener(message)
        }
        val loginUserId = IMCore.getService(UserService::class.java).getLoginUserId()
        if (!loginUserId.isNullOrEmpty()) {
            val uid = if (loginUserId == message.sendId) message.sendId else message.receiveId
            val oppoSite = if (loginUserId == message.sendId) message.receiveId else message.sendId
            IMCore.getService(ConversationService::class.java)
                .updateConversation(uid, oppoSite, message, notify)
        }
    }

    override suspend fun updateMessage(message: Msg, notify: Boolean) {
        message.originJson = message.toJson()
        insertOrReplace(message)
        if (notify) {
            MessageObserver.notifyMsgListener(message)
        }
    }

    override suspend fun deleteMessage(message: Msg) {
        withContext(Dispatchers.IO) {
            msgDao.delete(message)
        }
    }

    override suspend fun deleteMessageByChannel(channel: String) {
        withContext(Dispatchers.IO) {
            msgDao.deleteByChannel(channel)
        }
    }

    override suspend fun queryMessageList(count: Long, channel: String): List<Msg>? {
        return withContext(Dispatchers.IO) {
            msgDao.query(count, channel)?.map {
                it.message =
                    MessageUtils.parseCustomMsgToIMMessage(it.originJson!!, it.mark)
                it
            }?.reversed()
        }
    }

    override suspend fun queryMessageList(count: Long, message: Msg): List<Msg>? {
        return withContext(Dispatchers.IO) {
            msgDao.queryMessageByAnchorCondition(
                count,
                message.channel,
                message.messageId,
                message.timeStamp
            )?.map {
                it.message =
                    MessageUtils.parseCustomMsgToIMMessage(it.originJson!!, it.mark)
                it
            }?.reversed()
        }
    }

    override suspend fun queryMessageList(count: Long, userId: String, peerId: String): List<Msg>? {
        val channel =
            IMCore.getService(ConversationService::class.java).getChannelId(userId, peerId)
        return withContext(Dispatchers.IO) {
            msgDao.query(count, channel)
                ?.map {
                    it.message =
                        MessageUtils.parseCustomMsgToIMMessage(it.originJson!!, it.mark)
                    it
                }?.reversed()
        }
    }

    override suspend fun queryMessageByMessageId(messageId: String): Msg? {
        return withContext(Dispatchers.IO) {
            val msg = msgDao.query(messageId)
            if (msg != null) {
                msg.message =
                    MessageUtils.parseCustomMsgToIMMessage(msg.originJson!!, msg.mark)
            }
            msg
        }
    }

    override suspend fun queryRecentReceiveMessage(count: Long, userId: String): List<Msg>? {
        return withContext(Dispatchers.IO) {
            msgDao.queryMessageByReceive(count, userId)?.map {
                it.message =
                    MessageUtils.parseCustomMsgToIMMessage(it.originJson!!, it.mark)
                it
            }?.reversed()
        }
    }

    override suspend fun queryTextMessageList(count: Long, channel: String): List<Msg>? {
        return withContext(Dispatchers.IO) {
            msgDao.queryMessageByMessageType(count, channel, 1)?.map {
                it.message =
                    MessageUtils.parseCustomMsgToIMMessage(it.originJson!!, it.mark)
                it
            }?.reversed()
        }
    }
}