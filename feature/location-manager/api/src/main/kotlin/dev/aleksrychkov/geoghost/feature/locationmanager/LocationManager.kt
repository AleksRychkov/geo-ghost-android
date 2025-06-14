package dev.aleksrychkov.geoghost.feature.locationmanager

import dev.aleksrychkov.geoghost.core.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    companion object {
        lateinit var instance: LocationManager
    }

    suspend fun lastKnownLocation(): LatLng?
    fun location(): Flow<LatLng>
}
