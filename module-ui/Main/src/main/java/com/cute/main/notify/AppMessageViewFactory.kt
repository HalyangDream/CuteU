package com.cute.main.notify

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.collection.arrayMapOf
import androidx.core.view.marginTop
import com.cute.uibase.ActivityStack
import com.cute.uibase.route.RouteSdk
import com.cute.uibase.userbehavior.UserBehavior

object AppMessageViewFactory : ActivityStack.ActivityStateListener() {


    private val arrayMap = arrayMapOf<Activity, View>()
    private val timerRunnable = TimerRunnable()

    init {
        ActivityStack.addListener(this)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
        removeAnimView(activity)
    }

    fun consume(message: AppMessage) {
        val activity = ActivityStack.getTopActivity() ?: return
        val result = arrayMap.contains(activity)
        if (result) {
            updateView(activity, message)
        } else {
            createView(activity, message)
        }
    }

    private fun createView(activity: Activity, message: AppMessage) {
        val view = AppMessageNotifyView(activity)
        val parent = activity.window.decorView as ViewGroup
        val translationY = view.marginTop + view.height
        view.translationY = -translationY.toFloat()
        parent.addView(
            view, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        arrayMap[activity] = view
        view.setupNotifyUi(message)
        view.setTarget(activity)
        view.animate().translationY(translationY.toFloat()).setDuration(300).start()
        view.postDelayed(timerRunnable.apply { setView(view) }, 5000)
        view.setAppMessageNotifyListener(object : AppMessageNotifyView.AppMessageNotifyListener {
            override fun onClick(message: AppMessage) {
                RouteSdk.navigationChat(message.id, "notice")
                UserBehavior.setRootPage("notice")
                removeAnimView(view.target as Activity?)
            }

            override fun onGestureRemove() {
                removeAnimView(view.target as Activity?)
            }
        })
    }

    private fun updateView(activity: Activity, message: AppMessage) {
        val view = arrayMap[activity]!! as AppMessageNotifyView
        view.setupNotifyUi(message)
        view.setTarget(activity)
        view.removeCallbacks(timerRunnable.apply { setView(null) })
        view.postDelayed(timerRunnable.apply { setView(view) }, 5000)
    }


    private fun removeAnimView(activity: Activity?) {
        val result = arrayMap.contains(activity)
        if (result) {
            val view = arrayMap[activity]!! as AppMessageNotifyView
            val translationY = view.marginTop + view.height
            view.animate().translationY(-translationY.toFloat()).setDuration(300).start()
            view.setTarget(null)
            view.setAppMessageNotifyListener(null)
            view.removeCallbacks(timerRunnable.apply { setView(null) })
            arrayMap.remove(activity)
        }
    }

    class TimerRunnable : Runnable {
        private var view: AppMessageNotifyView? = null
        fun setView(view: AppMessageNotifyView?) {
            this.view = view
        }

        override fun run() {
            removeAnimView(view?.target as Activity?)
        }
    }

}