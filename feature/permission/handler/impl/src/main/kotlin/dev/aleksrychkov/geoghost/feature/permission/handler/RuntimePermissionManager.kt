package dev.aleksrychkov.geoghost.feature.permission.handler

import androidx.activity.ComponentActivity
import dev.aleksrychkov.geoghost.feature.permission.handler.domain.RuntimePermissionRequesterImpl
import dev.aleksrychkov.geoghost.feature.permission.handler.domain.RuntimePermissionStatusImpl

internal class RuntimePermissionManager(
    private val componentActivity: ComponentActivity,
    statusImpl: RuntimePermissionStatusImpl =
        RuntimePermissionStatusImpl(componentActivity),
    private val requesterImpl: RuntimePermissionRequesterImpl =
        RuntimePermissionRequesterImpl(componentActivity)
) : RuntimePermissionStatus by statusImpl, RuntimePermissionRequester by requesterImpl {

    fun unregister() {
        requesterImpl.unregister()
    }
}
