package com.cute.im.service

import com.cute.im.annotation.IMService
import com.cute.im.bean.Conversation
import com.cute.im.bean.Msg
import com.cute.im.service.impl.ConversationServiceImpl


/**
 * author : mac
 * date   : 2022/5/11
 *
 */
@IMService(ConversationServiceImpl::class)
interface ConversationService {

    fun getChannelId(uid: String, peerId: String): String

    /**
     * 根据userId和peerId生成一个会话
     * @param userId 自己的ID
     * @param peerId 对方的ID
     * @return 会话
     */
    fun generateConversation(userId: String, peerId: String): Conversation


    /**
     * 数据库里插入一个会话
     */
    suspend fun addConversation(conversation: Conversation)


    /**
     * 根据IM的消息添加或者更新会话到数据库
     * @param msg IM的消息
     * @param notify 是否通知会话变更的回调
     */
    suspend fun addOrUpdateConversation(
        uid: String, peerId: String, msg: Msg, notify: Boolean = true
    )

    /**
     * 更新会话
     */
    suspend fun updateConversation(uid: String, peerId: String, msg: Msg, notify: Boolean = true)

    /**
     * 获取一个会话
     * @param uid 自己的ID
     * @param peerId 对方ID
     * @return 会话
     */
    suspend fun getConversation(uid: String, peerId: String): Conversation?

    /**
     * 获取一个会话
     * @param channel 会话的唯一频道
     * @return 会话
     */
    suspend fun getConversation(channel: String): Conversation?

    /**
     * 根据userId获取会话列表
     * @param userId 一般是自己的ID
     * @param listener 会话列表的回调
     */
    suspend fun getAllConversation(userId: String): List<Conversation>?

    /**
     * 根据userId获取会话列表
     * @param count 获取会话的数据
     * @param userId 一般是自己的ID
     * @param listener 会话列表的回调
     */
    suspend fun getConversation(count: Long, userId: String): MutableList<Conversation>?

    /**
     * 根据Conversation获取会话列表
     * @param count 获取会话的数据
     * @param anchor 锚点数据
     * @param listener 会话列表的回调
     */
    suspend fun getConversationAnchor(
        count: Long, anchor: Conversation
    ): MutableList<Conversation>?

    /**
     * 根据peerId和userId获取会话
     */
    suspend fun getConversationInPeerIds(
        userId: String,
        peerId: Collection<String>,
    ): List<Conversation>?

    /**
     * 根据channel获取会话
     */
    suspend fun getConversationInChannel(channels: Array<String>): List<Conversation>?

    /**
     * 更新会话
     * @param conversation
     */
    suspend fun updateConversation(conversation: Conversation)

    /**
     * 删除会话
     */
    suspend fun deleteConversation(channel: String)

    suspend fun deleteConversation(userId: String, peerId: String)

    suspend fun deleteConversations(list: MutableList<Conversation>)

    /**
     * 根据uid获取未读数
     * @param uid 自己的ID
     * @param listener 未读数的回调
     */
    suspend fun getUnReadCount(uid: String): Int


    /**
     * 清除与peerId的未读数
     * @param peerId 聊天中对方的ID
     */
    suspend fun clearUnReadCountByPeer(uid: String, peerId: String)

    /**
     * 清除所有的未读数
     */
    suspend fun clearAllUnReadCount(uid: String)

    /**
     * 判断是否正在和peerId聊天
     * @param peerId 对方的ID
     * @return true 正在聊天 false 不在聊天
     */
    fun isChat(peerId: String): Boolean

    /**
     * 设置正在聊天的对象
     * @param peerId 对象ID
     */
    fun setChatUser(peerId: String)

    /**
     * 获取正在聊天的对象
     */
    fun getChattingId(): String?

    /**
     * 移除正在聊天的对象
     * @param peerId 对象ID
     */
    fun removeChatUser(peerId: String)
}