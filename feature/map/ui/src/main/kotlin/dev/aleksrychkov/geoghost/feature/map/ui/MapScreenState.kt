package dev.aleksrychkov.geoghost.feature.map.ui

import androidx.compose.runtime.Immutable
import dev.aleksrychkov.geoghost.core.model.LatLng

@Immutable
internal data class MapScreenState(
    val ghostLocation: LatLng? = null,
    val tmpLatLng: LatLng? = null,

    val shouldAskNotificationPermission: Boolean = false,
    val isNotificationPermissionModalEnabled: Boolean = false,

    val isLocationMockEnabled: Boolean = false,
    val isLocationMockModalEnabled: Boolean = false,

    val isLocationPermissionEnabled: Boolean = false,
    val isLocationPermissionModalEnabled: Boolean = false,

    val isMenuModalEnabled: Boolean = false,

    val isBookmarkModalEnabled: Boolean = false,
)
