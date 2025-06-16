package dev.aleksrychkov.geoghost.feature.locationmanager

import android.Manifest
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.locationmanager.data.LocationStorage
import dev.aleksrychkov.geoghost.feature.locationmanager.source.LocationSource
import dev.aleksrychkov.geoghost.feature.locationmanager.source.LocationSourceCallback
import dev.aleksrychkov.geoghost.feature.locationmanager.source.LocationSourceFactory
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

internal class LocationManagerImpl(
    private val locationSourceFactory: LocationSourceFactory,
    private val locationStorage: LocationStorage,
) : LocationManager {

    private companion object {
        const val TIMEOUT_MILLIS = 5000L
    }

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
    private val fusedLocationSource: LocationSource by lazy {
        locationSourceFactory.fusedLocationSource()
    }

    override fun location(): Flow<LatLng> = sharedLocationFlow

    private val sharedLocationFlow: SharedFlow<LatLng> = pollLocation()
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = sharedLocationFlowTimeout()),
            replay = 1
        )

    private fun pollLocation(): Flow<LatLng> = callbackFlow {
        fusedLocationSource.start(object : LocationSourceCallback {
            override fun onLatLngResult(latLng: LatLng) {
                trySend(latLng)
                storeLocation(latLng)
            }
        })
        awaitClose {
            fusedLocationSource.stop()
        }
    }

    override suspend fun lastKnownLocation(): LatLng? =
        locationStorage.getLastKnownLatLng()

    private fun storeLocation(latLng: LatLng) {
        scope.launch {
            locationStorage.setLastKnownLatLng(latLng)
        }
    }

    private fun sharedLocationFlowTimeout(): Long {
        val isLocationPermissionGranted = RuntimePermissionStatus.instance
            ?.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
            ?: false
        return if (isLocationPermissionGranted) TIMEOUT_MILLIS else 0L
    }
}
