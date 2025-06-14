package dev.aleksrychkov.geoghost.feature.locationmanager.source

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

internal class FusedLocationSource(
    private val context: Context,
) : LocationSource {

    private companion object {
        const val LOCATION_INTERVAL_MILLIS = 1000L
    }

    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val callback = locationSourceCallback ?: return
                for (loc in result.locations) {
                    LocationSourceMapper.map(loc).let(callback::onLatLngResult)
                }
            }
        }
    }
    private var locationSourceCallback: LocationSourceCallback? = null

    @SuppressLint("MissingPermission")
    override suspend fun start(callback: LocationSourceCallback) {
        val priority = getLocationPriority() ?: return
        this.locationSourceCallback = callback
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(priority, LOCATION_INTERVAL_MILLIS).build(),
            locationCallback,
            Looper.getMainLooper(),
        )
        fusedClient.lastLocation.await().let(LocationSourceMapper::map).let {
            locationSourceCallback?.onLatLngResult(it)
        }
    }

    override fun stop() {
        this.locationSourceCallback = null
        fusedClient.removeLocationUpdates(locationCallback)
    }

    private suspend fun getLocationPriority(): Int? = withContext(Dispatchers.Main) {
        val permissionStatus = RuntimePermissionStatus.instance ?: return@withContext null
        val fineGranted = permissionStatus
            .getStatus(Manifest.permission.ACCESS_FINE_LOCATION)
            .granted
        if (fineGranted) return@withContext Priority.PRIORITY_HIGH_ACCURACY
        val coarseGranted = permissionStatus
            .getStatus(Manifest.permission.ACCESS_COARSE_LOCATION)
            .granted
        if (coarseGranted) return@withContext Priority.PRIORITY_BALANCED_POWER_ACCURACY
        return@withContext null
    }
}