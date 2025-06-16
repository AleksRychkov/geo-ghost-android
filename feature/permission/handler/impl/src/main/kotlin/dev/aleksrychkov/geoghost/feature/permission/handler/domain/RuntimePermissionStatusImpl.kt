package dev.aleksrychkov.geoghost.feature.permission.handler.domain

import androidx.activity.ComponentActivity
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionStatus
import dev.aleksrychkov.geoghost.feature.permission.handler.data.RuntimePermissionStorage
import dev.aleksrychkov.geoghost.feature.permission.handler.model.RuntimePermissionResult
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.RuntimePermissionIsGranted
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.RuntimePermissionShouldShowRationale
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.checkMainThread

internal class RuntimePermissionStatusImpl(
    private val componentActivity: ComponentActivity
) : RuntimePermissionStatus {

    override suspend fun getStatus(permission: String): RuntimePermissionResult {
        checkMainThread()

        val isGranted = RuntimePermissionIsGranted.isGranted(componentActivity, permission)
        val shouldShowRationale =
            RuntimePermissionShouldShowRationale.shouldShow(componentActivity, permission)

        val storage = RuntimePermissionStorage.instance
        val wasRationalAllowed = storage.getShouldShowRational(permission)
        val shouldRequestSettings = wasRationalAllowed && !shouldShowRationale

        if (shouldShowRationale) {
            storage.setShouldShowRational(permission, true)
        }
        if (isGranted) {
            storage.setShouldShowRational(permission, false)
            return RuntimePermissionResult(
                granted = true,
                shouldShowRationale = false,
                shouldRequestSettings = false,
            )
        }

        return RuntimePermissionResult(
            granted = false,
            shouldShowRationale = shouldShowRationale,
            shouldRequestSettings = shouldRequestSettings,
        )
    }

    override fun isGranted(permission: String): Boolean =
        RuntimePermissionIsGranted.isGranted(componentActivity, permission)
}