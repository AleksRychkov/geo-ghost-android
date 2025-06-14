package dev.aleksrychkov.geoghost.feature.map.ui

import android.Manifest
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.locationghost.LocationGhost
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsHandler
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


internal class MapScreenViewModel : ViewModel(), DefaultLifecycleObserver {

    private val _state = MutableStateFlow(MapScreenState())
    val state = _state.asStateFlow()
    private var mapActionsHandler: GeoGhostMapActionsHandler? = null

    override fun onResume(owner: LifecycleOwner) {
        checkIsLocationPermissionGranted()
        checkIsPostNotificationPermissionGranted()
        checkIsLocationMockEnabled()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mapActionsHandler = null
    }


    fun attachMapHandler(handler: GeoGhostMapActionsHandler) {
        mapActionsHandler = handler
    }

    fun detachMapHandler() {
        mapActionsHandler = null
    }

    fun scrollToCurrentPositionClicked() {
        mapActionsHandler?.moveCameraToCurrentUserLocation()
    }

    fun onMapActionTmpLocationSet(latLng: LatLng) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(tmpLatLng = latLng))
        }
    }

    fun mapIsReady() {
        viewModelScope.launch {
            val ghostLatLng = LocationGhost.instance.currentGhostLatLng() ?: return@launch
            LocationGhost.instance.startLocationGhost(latLng = ghostLatLng)
            mapActionsHandler?.run {
                removeTmpMarker()
                setGhostLocation(ghostLatLng)
            }

            _state.emit(_state.value.copy(tmpLatLng = null, ghostLocation = ghostLatLng))
        }
    }

    fun startGhostLocation() {
        val ghostLatLng = _state.value.tmpLatLng ?: return
        viewModelScope.launch {
            mapActionsHandler?.removeTmpMarker()
            mapActionsHandler?.setGhostLocation(ghostLatLng)
            LocationGhost.instance.startLocationGhost(latLng = ghostLatLng)

            _state.emit(_state.value.copy(tmpLatLng = null, ghostLocation = ghostLatLng))
        }
    }

    fun stopGhostLocation() {
        mapActionsHandler?.removeGhostMarker()
        viewModelScope.launch {
            LocationGhost.instance.stopLocationGhost()

            _state.emit(_state.value.copy(ghostLocation = null))
        }
    }

    fun showLocationPermissionModal() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isLocationPermissionModalEnabled = true)
            )
        }
    }

    fun dismissLocationPermissionModal() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isLocationPermissionModalEnabled = false)
            )
        }
    }

    fun showLocationMockingModal() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isLocationMockModalEnabled = true)
            )
        }
    }

    fun dismissLocationMockingModal() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isLocationMockModalEnabled = false)
            )
        }
    }

    fun showPermissionLocationModal() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isNotificationPermissionModalEnabled = true))
        }
    }

    fun dismissPermissionLocationModal() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isNotificationPermissionModalEnabled = false))
        }
    }

    fun showMenuModal() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isMenuModalEnabled = true))
        }
    }

    fun dismissMenuModal() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isMenuModalEnabled = false))
        }
    }

    fun dismissMenuModalWithLatLng(latLng: LatLng) {
        mapActionsHandler?.moveCameraToLatLng(latLng)
        viewModelScope.launch {
            _state.emit(_state.value.copy(isMenuModalEnabled = false))
        }
    }

    fun dismissMenuModalWithBookmarkEntity(entity: BookmarkEntity) {
        mapActionsHandler?.run {
            moveCameraToLatLng(entity.latLng)
            if (entity.latLng != _state.value.ghostLocation) {
                removeTmpMarker()
                setTmpLocation(entity.latLng)
            }
        }
        viewModelScope.launch {
            _state.emit(_state.value.copy(isMenuModalEnabled = false))
        }
    }

    fun onTmpMarkerClick() {
        viewModelScope.launch {
            mapActionsHandler?.removeTmpMarker()
            _state.emit(
                _state.value.copy(tmpLatLng = null)
            )
        }
    }

    fun onGhostMarkerClick() {
        if (_state.value.ghostLocation == null) return
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isBookmarkModalEnabled = true)
            )
        }
    }

    fun dismissBookmarkModal() {
        viewModelScope.launch {
            _state.emit(
                _state.value.copy(isBookmarkModalEnabled = false)
            )
        }
    }

    private fun checkIsLocationPermissionGranted() {
        val permissionStatus = RuntimePermissionStatus.instance ?: return
        viewModelScope.launch {
            val result = permissionStatus.getStatus(Manifest.permission.ACCESS_FINE_LOCATION)
            _state.emit(
                _state.value.copy(
                    isLocationPermissionEnabled = result.granted,
                )
            )
        }
    }

    private fun checkIsPostNotificationPermissionGranted() {
        val permissionStatus = RuntimePermissionStatus.instance ?: return
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val result = permissionStatus.getStatus(Manifest.permission.POST_NOTIFICATIONS)
                val shouldAsk = if (result.granted) false else !result.shouldRequestSettings
                _state.emit(
                    _state.value.copy(
                        shouldAskNotificationPermission = shouldAsk,
                    )
                )
            } else {
                _state.emit(
                    _state.value.copy(
                        shouldAskNotificationPermission = false,
                    )
                )
            }
        }
    }

    private fun checkIsLocationMockEnabled() {
        viewModelScope.launch {
            val isEnabled = LocationGhost.instance.isLocationMockEnabled()
            val isGhostServiceRunning = LocationGhost.instance.isRunning()
            if (!isEnabled && isGhostServiceRunning) {
                stopGhostLocation()
            }
            _state.emit(
                _state.value.copy(isLocationMockEnabled = isEnabled)
            )
        }
    }
}
