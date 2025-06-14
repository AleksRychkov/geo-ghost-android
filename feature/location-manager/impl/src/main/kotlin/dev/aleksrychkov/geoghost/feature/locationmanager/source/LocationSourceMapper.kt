package dev.aleksrychkov.geoghost.feature.locationmanager.source

import android.location.Location
import dev.aleksrychkov.geoghost.core.model.LatLng

internal object LocationSourceMapper {
    fun map(location: Location): LatLng =
        LatLng(latitude = location.latitude, longitude = location.longitude)
}