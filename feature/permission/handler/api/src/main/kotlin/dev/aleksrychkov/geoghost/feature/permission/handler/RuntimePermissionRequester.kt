package dev.aleksrychkov.geoghost.feature.permission.handler

interface RuntimePermissionRequester {

    companion object {
        var instance: RuntimePermissionRequester? = null
    }

    suspend fun request(permission: String): Boolean
    suspend fun request(permissions: Array<String>): Map<String, Boolean>
}
