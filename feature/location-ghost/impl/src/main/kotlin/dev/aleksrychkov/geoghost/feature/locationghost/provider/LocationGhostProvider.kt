package dev.aleksrychkov.geoghost.feature.locationghost.provider

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.util.Log
import dev.aleksrychkov.geoghost.core.exception.safeRunCatching
import dev.aleksrychkov.geoghost.feature.locationghost.data.LocationGhostStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal interface LocationGhostProvider {
    companion object {
        lateinit var instance: LocationGhostProvider
        fun initialize(context: Context) {
            instance = LocationGhostProviderImpl(
                context = context,
                timeProvider = TimeProvider()
            )
        }
    }

    fun start(): LocationGhostProviderResult
    fun stop()
    fun isLocationMockEnabled(): Boolean
}

private class LocationGhostProviderImpl(
    context: Context,
    private val timeProvider: TimeProvider
) : LocationGhostProvider {

    private companion object {
        const val TAG = "LocationGhostProvider"
        const val GHOST_DURATION_SECONDS = 3
        const val GHOST_LOCATION_ACCURACY = 3f
        const val POWER_USAGE_LOW: Int = 1
        const val ACCURACY_COARSE: Int = 2
    }

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var isStarted: Boolean = false
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun start(): LocationGhostProviderResult {
        if (isStarted) return LocationGhostProviderResult.SUCCESS
        try {
            safeRunCatching {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
            }
            if (!installTestProvider()) {
                return LocationGhostProviderResult.ERROR_SET_APP_AS_MOCK_LOCATION_APP
            }

            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            Log.d(TAG, "Mock location provider started: ${LocationManager.GPS_PROVIDER}")
            ghostLocation()
            isStarted = true
            return LocationGhostProviderResult.SUCCESS
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start", e)
            return LocationGhostProviderResult.ERROR
        }
    }

    override fun stop() {
        if (!isStarted) return
        try {
            isStarted = false
            scope.coroutineContext.cancelChildren()

            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false)
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)

            Log.d(TAG, "Mock location provider stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop", e)
        }
    }

    override fun isLocationMockEnabled(): Boolean = installTestProvider()

    private fun installTestProvider(): Boolean {
        val powerUsage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ProviderProperties.POWER_USAGE_LOW
        } else {
            POWER_USAGE_LOW
        }
        val accuracy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ProviderProperties.ACCURACY_COARSE
        } else {
            ACCURACY_COARSE
        }

        try {
            locationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false, // requiresNetwork
                false, // requiresSatellite
                false, // requiresCell
                false, // hasMonetaryCost
                true,  // supportsAltitude
                false,  // supportsSpeed
                false,  // supportsBearing
                powerUsage, // powerRequirement
                accuracy, // accuracy
            )
            return true
        } catch (se: SecurityException) {
            Log.e(TAG, "Unable to install test provider", se)
            return false
        }
    }

    private fun ghostLocation() {
        scope.launch {
            while (isActive) {
                setGhostLocation()
                delay(GHOST_DURATION_SECONDS.seconds)
            }
        }
    }

    private suspend fun setGhostLocation() {
        val currentGhostLocation = LocationGhostStorage.instance.getCurrentLocation()
        if (currentGhostLocation == null) {
            Log.e(TAG, "No location to mock")
            return
        }
        try {
            val location = Location(LocationManager.GPS_PROVIDER).apply {
                latitude = currentGhostLocation.latitude
                longitude = currentGhostLocation.longitude
                accuracy = GHOST_LOCATION_ACCURACY
                time = timeProvider.currentTimeInMillis()
                elapsedRealtimeNanos = timeProvider.elapsedRealtimeNanos()
            }
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setGhostLocation", e)
        }
    }
}
