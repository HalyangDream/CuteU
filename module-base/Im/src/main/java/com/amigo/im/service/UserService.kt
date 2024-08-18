package com.amigo.im.service

import com.amigo.im.annotation.IMService
import com.amigo.im.bean.User
import com.amigo.im.listener.UserCacheConfig
import com.amigo.im.service.impl.UserServiceImpl

/**
 * author : mac
 * date   : 2022/5/11
 *
 */
@IMService(UserServiceImpl::class)
interface UserService {

    fun isLogin(): Boolean

    fun getLoginUserId():String?

    fun login(userId: String?, token: String?)

    fun logout()

    /**
     * 设置用户缓存配置
     * @param config 配置
     */
    fun setupUserConfig(config: UserCacheConfig?)

    /**
     * 获取用户信息
     * @param userId
     * @param listener 用户信息的回调
     */
    suspend fun getUserInfo(userId: String):User?

    /**
     * 根据ID获取User
     * @param id 需要获取用户信息的ID
     * @return IMUser IM中内部的信息
     */
    suspend fun getUser(id: String): User?

    /**
     * 设置用户信息到本地数据库
     * @param user 用户的信息
     */
    suspend fun setUser(user: User)

    /**
     * 从数据库删除用户
     * @param user 需要删除的用户
     */
    suspend fun deleteUser(user: User)
}