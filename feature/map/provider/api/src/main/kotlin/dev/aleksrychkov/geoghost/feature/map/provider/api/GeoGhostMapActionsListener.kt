package dev.aleksrychkov.geoghost.feature.map.provider.api

import dev.aleksrychkov.geoghost.core.model.LatLng

interface GeoGhostMapActionsListener {
    fun setTmpLocation(location: LatLng)
    fun mapIsReady()
    fun onTmpMarkerClick()
    fun onGhostMarkerClick()
}
