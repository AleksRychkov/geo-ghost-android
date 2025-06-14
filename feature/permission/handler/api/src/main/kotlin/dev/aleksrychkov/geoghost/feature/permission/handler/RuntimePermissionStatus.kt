package dev.aleksrychkov.geoghost.feature.permission.handler

import dev.aleksrychkov.geoghost.feature.permission.handler.model.RuntimePermissionResult

interface RuntimePermissionStatus {

    companion object {
        @Volatile
        var instance: RuntimePermissionStatus? = null
    }

    suspend fun getStatus(permission: String): RuntimePermissionResult
}
