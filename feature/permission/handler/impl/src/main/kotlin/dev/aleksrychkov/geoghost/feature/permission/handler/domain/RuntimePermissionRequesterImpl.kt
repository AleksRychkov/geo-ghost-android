package dev.aleksrychkov.geoghost.feature.permission.handler.domain

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionRequester
import dev.aleksrychkov.geoghost.feature.permission.handler.utils.checkMainThread
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

internal class RuntimePermissionRequesterImpl(
    componentActivity: ComponentActivity,
) : RuntimePermissionRequester {

    private var deferredResult: CompletableDeferred<Boolean>? = null
    private var deferredMultipleResult: CompletableDeferred<Map<String, Boolean>>? = null

    private val requestLauncher: ActivityResultLauncher<String> = componentActivity
        .registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            deferredResult?.complete(isGranted)
        }

    private val requestMultipleLauncher: ActivityResultLauncher<Array<String>> = componentActivity
        .registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            deferredMultipleResult?.complete(permissions)
        }

    override suspend fun request(permission: String): Boolean {
        checkMainThread()
        deferredResult = CompletableDeferred()
        requestLauncher.launch(permission)
        val result = deferredResult?.await() ?: false
        coroutineContext.ensureActive()
        return result
    }

    override suspend fun request(permissions: Array<String>): Map<String, Boolean> {
        checkMainThread()
        deferredMultipleResult = CompletableDeferred()
        requestMultipleLauncher.launch(permissions)
        val result: Map<String, Boolean> =
            deferredMultipleResult?.await() ?: permissions.associateWith { false }
        coroutineContext.ensureActive()
        return result
    }

    fun unregister() {
        requestLauncher.unregister()
        requestMultipleLauncher.unregister()
    }
}