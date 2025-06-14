package dev.aleksrychkov.geoghost.feature.locationghost.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.feature.locationghost.LocationGhost
import dev.aleksrychkov.geoghost.feature.locationghost.LocationGhostImpl
import dev.aleksrychkov.geoghost.feature.locationghost.data.LocationGhostStorage
import dev.aleksrychkov.geoghost.feature.locationghost.provider.LocationGhostProvider
import dev.aleksrychkov.geoghost.feature.locationghost.service.LocationGhostServiceNotificationManager

@Suppress("unused")
internal class LocationGhostInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        LocationGhostProvider.initialize(context)
        LocationGhostStorage.initialize(context)
        LocationGhostServiceNotificationManager.initialize(context)
        LocationGhost.instance = LocationGhostImpl(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}