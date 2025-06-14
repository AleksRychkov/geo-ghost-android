package dev.aleksrychkov.geoghost.feature.locationghost

import dev.aleksrychkov.geoghost.core.model.LatLng

interface LocationGhost {
    companion object {
        lateinit var instance: LocationGhost
    }

    suspend fun currentGhostLatLng(): LatLng?
    suspend fun startLocationGhost(latLng: LatLng)
    suspend fun stopLocationGhost()
    suspend fun isLocationMockEnabled(): Boolean
    suspend fun isRunning(): Boolean
}
