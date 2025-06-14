package dev.aleksrychkov.geoghost.feature.permission.ui.location

import android.Manifest
import android.content.Context
import dev.aleksrychkov.geoghost.feature.permission.ui.AbstractPermissionViewModel
import dev.aleksrychkov.geoghost.feature.permission.ui.R

internal class LocationPermissionViewModel(
    closeSelf: () -> Unit,
) : AbstractPermissionViewModel(closeSelf) {

    override fun mainPermission(): String =
        Manifest.permission.ACCESS_FINE_LOCATION

    override fun settingsNavigateToMessage(context: Context): String =
        context.resources.getString(R.string.location_navigate_to_permissions)

    override suspend fun request(context: Context): Boolean {
        val res = permissionRequester.request(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        return res.any { it.value }
    }
}
