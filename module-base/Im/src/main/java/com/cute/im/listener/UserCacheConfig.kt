package com.cute.im.listener

import com.cute.im.bean.User


interface UserCacheConfig {


    /**
     * 设置网络请求更新缓存
     */
    suspend fun updateCache(userId: String?): User?


    /**
     * 更新缓存时间
     * 秒
     */
    fun updateCacheTime(): Long
}