package com.cute.uibase

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.NoSuchElementException
import java.util.Vector
import java.util.concurrent.CopyOnWriteArrayList

object ActivityStack : Application.ActivityLifecycleCallbacks {


    lateinit var application: Application
    private var foregroundCount = 0 // 位于前台的 Activity 的数目
    private val activities: CopyOnWriteArrayList<Activity> = CopyOnWriteArrayList()
    private val stateListener = CopyOnWriteArrayList<ActivityStateListener>()


    fun isBackground() = foregroundCount <= 0

    fun getTopActivity(): Activity? {
        try {
            return activities.lastOrNull()
        } catch (ex: ArrayIndexOutOfBoundsException) {
            ex.printStackTrace()
        } catch (ex:Exception){
            ex.printStackTrace()
        }
        return null
    }

    fun addListener(listener: ActivityStateListener) {
        stateListener.add(listener)
    }

    fun removeListener(listener: ActivityStateListener) {
        stateListener.remove(listener)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
        for (activityStateListener in stateListener) {
            activityStateListener.onActivityCreated(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        for (activityStateListener in stateListener) {
            activityStateListener.onActivityStarted(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        foregroundCount++
        for (activityStateListener in stateListener) {
            activityStateListener.onActivityResumed(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        foregroundCount--
        for (activityStateListener in stateListener) {
            activityStateListener.onActivityPaused(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        for (activityStateListener in stateListener) {
            activityStateListener.onActivityStopped(activity)
        }
    }


    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
        for (activityStateListener in stateListener) {
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
}