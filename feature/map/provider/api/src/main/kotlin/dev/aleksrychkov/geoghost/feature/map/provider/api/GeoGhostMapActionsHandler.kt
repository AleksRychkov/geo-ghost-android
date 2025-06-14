package dev.aleksrychkov.geoghost.feature.map.provider.api

import dev.aleksrychkov.geoghost.core.model.LatLng

interface GeoGhostMapActionsHandler {
    fun moveCameraToCurrentUserLocation()
    fun moveCameraToLatLng(latLng: LatLng)
    fun removeTmpMarker()
    fun removeGhostMarker()
    fun setGhostLocation(latLng: LatLng)
    fun setTmpLocation(latLng: LatLng)
}
