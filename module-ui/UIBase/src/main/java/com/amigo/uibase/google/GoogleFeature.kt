package com.amigo.uibase.google

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


object GoogleFeature {



    fun checkAppUpdate(
        activity: Activity,
        block: (result: Boolean, AppUpdateManager, AppUpdateInfo) -> Unit
    ) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                block(true, appUpdateManager, appUpdateInfo)
            } else {
                block(false, appUpdateManager, appUpdateInfo)
            }
        }
    }

    fun startUpdateApp(
        manager: AppUpdateManager,
        updateInfo: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        // Request the update.
        manager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            updateInfo,
            // an activity result launcher registered via registerForActivityResult
            launcher,
            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
            // flexible updates.
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        )
    }

}