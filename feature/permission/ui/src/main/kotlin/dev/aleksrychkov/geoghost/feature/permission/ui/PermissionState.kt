package dev.aleksrychkov.geoghost.feature.permission.ui

import androidx.compose.runtime.Immutable

@Immutable
internal data class PermissionState(
    val isLoading: Boolean = true,
    val granted: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val shouldRequestSettings: Boolean = false,
)
