package com.cute.tool

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {
    /**
     * 隐藏软键盘
     *
     * @param context
     */
    fun hide(context: Context, view: View?) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    /**
     * 强制隐藏软键盘
     *
     * @param activity
     */
    fun hideForce(activity: Activity?) {
        try {
            if (activity != null && activity.currentFocus != null) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0) //强制隐藏键盘
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    fun show(activity: Context, view: View?) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}