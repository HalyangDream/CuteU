package com.amigo.tool

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.lang.reflect.Field

@SuppressLint("StaticFieldLeak")
object Toaster {

    private var sFieldTN: Field? = null
    private var sFieldTNHandler: Field? = null

    init {
        try {
            sFieldTN = Toast::class.java.getDeclaredField("mTN")
            sFieldTN?.isAccessible = true
            sFieldTNHandler = sFieldTN?.type?.getDeclaredField("mHandler")
            sFieldTNHandler?.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun hook(toast: Toast) {
        try {
            val tn = sFieldTN!![toast]
            val preHandler = sFieldTNHandler!![tn] as Handler
            sFieldTNHandler!![tn] = SafelyHandlerWrapper(preHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private class SafelyHandlerWrapper internal constructor(private val impl: Handler) :
        Handler(Looper.getMainLooper()) {
        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun handleMessage(msg: Message) {
            impl.handleMessage(msg)
        }
    }


    private fun showToast(context: Context, text: String, duration: Int) {
        if (isFastDoubleClick() && text == mLastText) return
        try {
            val toast = Toast.makeText(context, text, duration)
            // 在调用Toast.show()之前处理:
            hook(toast)
            toast.show()
            mLastText = text
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun showToast(context: Context, stringId: Int, duration: Int) {
        if (isFastDoubleClick() && context.getString(stringId) == mLastText) return
        try {
            val toast = Toast.makeText(context, stringId, duration)
            // 在调用Toast.show()之前处理:
            hook(toast)
            toast.show()
            mLastText = context.getString(stringId)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun showShort(context: Context, resId: Int) {
        showToast(context, resId, Toast.LENGTH_SHORT)
    }

    fun showShort(context: Context, text: String) {
        showToast(context, text, Toast.LENGTH_SHORT)
    }

    fun showLong(context: Context, text: String) {
        showToast(context, text, Toast.LENGTH_LONG)
    }

    fun showLong(context: Context, resId: Int) {
        showToast(context, resId, Toast.LENGTH_LONG)
    }

    private const val TIME: Long = 800
    private var lastClickTime: Long = 0
    private var mLastText: String? = null

    private fun isFastDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        if (time - lastClickTime < TIME) {
            return true
        }
        lastClickTime = time
        return false
    }
}