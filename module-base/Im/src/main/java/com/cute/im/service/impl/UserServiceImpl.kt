package  com.cute.im.service.impl

import com.cute.im.DbManager
import com.cute.im.MessageObserver
import com.cute.im.bean.User
import com.cute.im.dao.UserDao
import com.cute.im.listener.UserCacheConfig
import com.cute.im.rtm.RtmManager
import com.cute.im.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * author : mac
 * date   : 2022/5/11
 *
 */
class UserServiceImpl : UserService {

    private var config: UserCacheConfig? = null
    private var isLogin = false
    private var loginUserId: String? = ""

    private val userDao by lazy { DbManager.get().getDao(UserDao::class.java) }

    override fun isLogin(): Boolean {
        return isLogin
    }

    override fun getLoginUserId(): String? {
        return loginUserId
    }

    override fun login(userId: String?, token: String?) {
        if (userId.isNullOrEmpty() || token.isNullOrEmpty()) return
        this.loginUserId = userId
        RtmManager.getInstance().loginRtm(token, userId) {
            isLogin = it == 0
            if (isLogin) {
                MessageObserver.notifyLoginSuccessListener()
            }
        }
    }

    override fun logout() {
        isLogin = false
        loginUserId = ""
        RtmManager.getInstance().logoutRtm {

        }
    }

    override fun setupUserConfig(config: UserCacheConfig?) {
        this.config = config
    }

    override suspend fun getUserInfo(userId: String): User? {
        val user = withContext(Dispatchers.IO) {
            var info = userDao.queryUser(userId)
            val lastUpdateTime = (System.currentTimeMillis() - (info?.time ?: 0L)) / 1000
            val updateIntervalTime =
                if (config != null) config!!.updateCacheTime() else 12 * 60 * 60
            val isExpired = lastUpdateTime >= updateIntervalTime
            if (info == null || isExpired) {
                info = updateUserByNet(userId)
            }
            info
        }
        return user
    }

    override suspend fun getUser(id: String): User? {
        val user = userDao.queryUser(id)
        user?.toMap()
        return user
    }


    override suspend fun setUser(user: User) {
        user.time = System.currentTimeMillis()
        user.toExtra()
        insertOrUpdate(user)
    }

    override suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }


//    override fun deleteUser(user: IMUser?) {
//        IMUserDbManager.get().deleteUser(user)
//    }


    private suspend fun updateUserByNet(userId: String): User? {
        if (config == null || userId.isEmpty()) {

            return null
        }

        return withContext(Dispatchers.IO) {
            val temp = config?.updateCache(userId)
            temp?.uid = userId
            temp?.time = System.currentTimeMillis()
            temp?.toExtra()
            if (temp != null) {
                DbManager.get().getDao(UserDao::class.java).insert(temp)
            }
            temp
        }
    }


    private fun insertOrUpdate(user: User) {
        user.time = System.currentTimeMillis()
        val row = DbManager.get().getDao(UserDao::class.java).update(user)
        if (row == 0) {
            DbManager.get().getDao(UserDao::class.java).insert(user)
        }
    }

}