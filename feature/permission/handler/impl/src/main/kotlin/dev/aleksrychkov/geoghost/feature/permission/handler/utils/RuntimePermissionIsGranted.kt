package dev.aleksrychkov.geoghost.feature.permission.handler.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object RuntimePermissionIsGranted {

    fun isGranted(context: Context, permission: String): Boolean {
        if (permission == Manifest.permission.POST_NOTIFICATIONS) {
            return isNotificationGranted(context)
        }
        return context.isGrunted(permission)
    }

    private fun isNotificationGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun Context.isGrunted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}