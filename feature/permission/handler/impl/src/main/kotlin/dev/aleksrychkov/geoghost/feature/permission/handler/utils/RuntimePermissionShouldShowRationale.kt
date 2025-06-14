package dev.aleksrychkov.geoghost.feature.permission.handler.utils

import android.Manifest
import android.app.Activity
import android.os.Build

object RuntimePermissionShouldShowRationale {

    fun shouldShow(activity: Activity, permission: String): Boolean {
        if (permission == Manifest.permission.POST_NOTIFICATIONS) {
            return notificationRationale(activity)
        }
        return activity.shouldShowRequestPermissionRationale(permission)
    }

    private fun notificationRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            false
        }
    }
}
