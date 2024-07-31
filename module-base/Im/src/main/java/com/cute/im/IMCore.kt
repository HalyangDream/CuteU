package com.cute.im

import android.content.Context
import android.util.Log
import com.cute.im.annotation.IMService
import com.cute.im.cutom.CustomMessage
import com.cute.im.cutom.CustomNotify
import com.cute.im.rtm.RtmManager
import io.agora.rtm.RtmCallManager
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

/**
 * author : mac
 * date   : 2022/4/13
 *
 */
object IMCore {


    private const val TAG = "[ IMCore ]"
    private val serviceMap by lazy { mutableMapOf<Class<*>, Any>() }

    private fun String.printLog(priority: Int = Log.VERBOSE) {
        Log.println(priority, TAG, this)
    }

    fun getTelephoneService(): RtmCallManager? = RtmManager.getInstance().getCallManager()


    fun <T> getService(clazz: Class<T>): T {
        synchronized(serviceMap) {
            val obj = serviceMap[clazz]
            if (obj != null) {
                return obj as T
            }
        }
        val implClass = getClassLoader(clazz)
        val implObj = implClass.getDeclaredConstructor().newInstance()
        synchronized(serviceMap) {
            val proxyObj = Proxy.newProxyInstance(
                clazz.classLoader,
                arrayOf(clazz), IMInvocationHandler(implObj)
            )
            serviceMap[clazz] = proxyObj
            return proxyObj as T
        }
    }


    private class IMInvocationHandler constructor(val target: Any?) : InvocationHandler {

        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
            return method?.invoke(target, *(args ?: arrayOfNulls<Any>(0)))
        }
    }

    private fun <T> getClassLoader(clazz: Class<T>): Class<*> {
        if (!clazz.isInterface) {
            throw IllegalArgumentException("$TAG Class must Interface")
        }
        val isAnnotation = clazz.isAnnotationPresent(IMService::class.java)
        if (!isAnnotation) {
            throw IllegalArgumentException("$TAG ${clazz.simpleName}Class not use")
        }
        val annotation = clazz.getAnnotation(IMService::class.java)
        val kClazz = annotation?.impl
        if (kClazz == null || kClazz == Void::class) {
            throw IllegalArgumentException("$TAG ${kClazz?.java?.simpleName} Class not use")
        }
        val isAssignableFrom = clazz.isAssignableFrom(kClazz.java)
        if (!isAssignableFrom) {
            throw IllegalArgumentException("$TAG $kClazz Class Not belong to ${clazz.simpleName}")
        }
        return kClazz.java
    }

    fun initSdk(context: Context, agoraId: String) {
        RtmManager.getInstance().initialize(context, agoraId)
        MessageObserver.init()
    }

    fun initDbAndListener(context: Context) {
        DbManager.get().initDb(context)
    }

    fun release() {
        MessageObserver.release()
    }

    /**
     * 注册消息
     */
    fun registerMessageType(vararg messages: CustomMessage) {
        for (message in messages) {
            MessageUtils.registerMessages.add(message)
        }
    }

    /**
     * 注册通知
     */
    fun registerNotifyType(vararg customNotifys: CustomNotify) {
        for (notify in customNotifys) {
            MessageUtils.registerNotify.add(notify)
        }
    }
}