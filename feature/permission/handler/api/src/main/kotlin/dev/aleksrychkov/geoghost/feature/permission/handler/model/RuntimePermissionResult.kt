package dev.aleksrychkov.geoghost.feature.permission.handler.model

data class RuntimePermissionResult(
    val granted: Boolean,
    val shouldShowRationale: Boolean,
    val shouldRequestSettings: Boolean,
)
