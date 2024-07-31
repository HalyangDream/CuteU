package com.cute.basic.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

object StatusUtils {
    @SuppressLint("InternalInsetResource")
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId);
        }
        return result
    }


    fun setImmerseLayout(view: View, activity: Activity) {// view为标题栏
        //当版本是4.4以上
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        val statusBarHeight = getStatusBarHeight(activity)
        view.setPadding(0, statusBarHeight, 0, 0)
        val systemUiVisibility = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            window.navigationBarColor = Color.BLACK
        }
    }

    fun setImmerseLayout(view: View, fragment: Fragment) {// view为标题栏
        //当版本是4.4以上
        val window = fragment.activity?.window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = Color.TRANSPARENT
        val statusBarHeight = getStatusBarHeight(fragment.requireContext())
        view.setPadding(0, statusBarHeight, 0, 0)
        val systemUiVisibility = window?.decorView?.systemUiVisibility
        window?.decorView?.systemUiVisibility =
            systemUiVisibility!! or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            window.navigationBarColor =Color.BLACK
        }
    }

    fun setStatusMode(isDarkMode: Boolean, window: Window) {
        val decorView = window.decorView
        if (isDarkMode) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        decorView.systemUiVisibility =
            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            window.navigationBarColor =Color.BLACK
        }
    }


    fun setStatusBarColor(color: Int, window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
        val systemUiVisibility = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            window.navigationBarColor =Color.BLACK
        }
    }
}