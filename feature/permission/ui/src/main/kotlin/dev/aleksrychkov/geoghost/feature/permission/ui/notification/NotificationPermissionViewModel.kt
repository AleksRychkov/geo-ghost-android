package dev.aleksrychkov.geoghost.feature.permission.ui.notification

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import dev.aleksrychkov.geoghost.feature.permission.ui.AbstractPermissionViewModel
import dev.aleksrychkov.geoghost.feature.permission.ui.R

internal class NotificationPermissionViewModel(
    closeSelf: () -> Unit,
) : AbstractPermissionViewModel(closeSelf) {

    @SuppressLint("InlinedApi")
    override fun mainPermission(): String =
        Manifest.permission.POST_NOTIFICATIONS

    override fun settingsNavigateToMessage(context: Context): String =
        context.getString(R.string.notification_navigate_to_notifications)

    override suspend fun request(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequester.request(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
}
