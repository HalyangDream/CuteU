package com.cute.uibase

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.NoSuchElementException
import java.util.Vector
import java.util.concurrent.CopyOnWriteArrayList

object ActivityStack : Application.ActivityLifecycleCallbacks {


    lateinit var application: Application
    private val activities: CopyOnWriteArrayList<Activity> = CopyOnWriteArrayList()
    private val activityStateListeners = CopyOnWriteArrayList<ActivityStateListener>()
    private val appStateListeners = CopyOnWriteArrayList<AppStateListener>()
    private var foregroundCount = 0 // 位于前台的 Activity 的数目
    fun isBackground() = foregroundCount <= 0

    fun getTopActivity(): Activity? {
        try {
            return activities.lastOrNull()
        } catch (ex: ArrayIndexOutOfBoundsException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun addAppStateListener(listener: AppStateListener) {
        appStateListeners.add(listener)
    }

    fun addActivityStateListener(listener: ActivityStateListener) {
        activityStateListeners.add(listener)
    }

    fun removeAppStateListener(listener: AppStateListener) {
        appStateListeners.remove(listener)
    }

    fun removeActivityStateListener(listener: ActivityStateListener) {
        activityStateListeners.remove(listener)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityCreated(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (foregroundCount <= 0) {
            for (appStateListener in appStateListeners) {
                appStateListener.onAppFront()
            }
        }
        foregroundCount++
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityStarted(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityResumed(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityPaused(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        foregroundCount--
        if (foregroundCount <= 0) {
            for (appStateListener in appStateListeners) {
                appStateListener.onAppBackground()
            }
        }
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityStopped(activity)
        }
    }


    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
        for (activityStateListener in activityStateListeners) {
            activityStateListener.onActivityDestroyed(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    open class ActivityStateListener {
        open fun onActivityCreated(activity: Activity) {}
        open fun onActivityStarted(activity: Activity) {}
        open fun onActivityResumed(activity: Activity) {}
        open fun onActivityPaused(activity: Activity) {}
        open fun onActivityStopped(activity: Activity) {}
        open fun onActivityDestroyed(activity: Activity) {}
    }

    open class AppStateListener {
        open fun onAppFront() {}

        open fun onAppBackground() {}
    }
}