package com.amigo.uibase

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference


fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Context.screenWidth() = this.resources.displayMetrics.widthPixels

fun Context.screenHeight() = this.resources.displayMetrics.heightPixels

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 */
private val clickMap by lazy { mutableMapOf<Int, Long?>() }
fun View.setThrottleListener(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        val lastTime = clickMap[it.id] ?: 0
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > interval) {
            clickMap[it.id] = currentTime
            action(it)
            return@setOnClickListener
        }
    }
}

fun ImageView.setOnlinePointImage(onlineStatus: Int) {
    when (onlineStatus) {
        2 -> setImageResource(R.drawable.ic_point_busy)
        3 -> setImageResource(R.drawable.ic_point_online)
        else -> setImageResource(R.drawable.ic_point_offline)
    }
}

fun ImageView.setOnlineLabelImage(onlineStatus: Int) {
    when (onlineStatus) {
        2 -> setImageResource(R.drawable.ic_label_busy)
        3 -> setImageResource(R.drawable.ic_label_online)
        else -> setImageResource(R.drawable.ic_label_offline)
    }
}