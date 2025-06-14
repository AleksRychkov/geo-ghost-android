package dev.aleksrychkov.geoghost.feature.locationghost

import android.content.Context
import android.content.Intent
import android.os.Build
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.locationghost.data.LocationGhostStorage
import dev.aleksrychkov.geoghost.feature.locationghost.provider.LocationGhostProvider
import dev.aleksrychkov.geoghost.feature.locationghost.service.LocationGhostService
import dev.aleksrychkov.geoghost.feature.locationghost.service.LocationGhostServiceActions

internal class LocationGhostImpl(
    private val context: Context
) : LocationGhost {

    override suspend fun currentGhostLatLng(): LatLng? {
        return LocationGhostStorage.instance.getCurrentLocation()
    }

    override suspend fun startLocationGhost(latLng: LatLng) {
        LocationGhostStorage.instance.setCurrentLocation(latLng)

        // start service
        runLocationServiceAction(LocationGhostServiceActions.START)
    }

    override suspend fun stopLocationGhost() {
        LocationGhostStorage.instance.setCurrentLocation(null)

        // stop service
        runLocationServiceAction(LocationGhostServiceActions.STOP)
    }

    override suspend fun isLocationMockEnabled(): Boolean {
        return LocationGhostProvider.instance.isLocationMockEnabled()
    }

    override suspend fun isRunning(): Boolean {
        return LocationGhostStorage.instance.inServiceRunning()
    }

    private suspend fun runLocationServiceAction(action: LocationGhostServiceActions) {
        val isServiceRunning = LocationGhostStorage.instance.inServiceRunning()
        if (!isServiceRunning && action == LocationGhostServiceActions.STOP) return
        Intent(context, LocationGhostService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
                return
            }
            context.startService(it)
        }
    }
}
