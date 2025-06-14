package dev.aleksrychkov.geoghost.feature.locationmanager.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.feature.locationmanager.LocationManager
import dev.aleksrychkov.geoghost.feature.locationmanager.LocationManagerImpl
import dev.aleksrychkov.geoghost.feature.locationmanager.data.LocationStorage
import dev.aleksrychkov.geoghost.feature.locationmanager.source.LocationSourceFactory

@Suppress("unused")
internal class LocationManagerInitializer : Initializer<LocationManager> {

    override fun create(context: Context): LocationManager {
        val sourceFactory = LocationSourceFactory(context)
        val locationStorage = LocationStorage(context)
        LocationManager.instance = LocationManagerImpl(
            locationSourceFactory = sourceFactory,
            locationStorage = locationStorage,
        )

        return LocationManager.instance
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
