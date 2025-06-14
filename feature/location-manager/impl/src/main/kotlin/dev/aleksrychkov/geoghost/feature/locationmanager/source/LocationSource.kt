package dev.aleksrychkov.geoghost.feature.locationmanager.source

import dev.aleksrychkov.geoghost.core.model.LatLng

internal interface LocationSource {
    suspend fun start(callback: LocationSourceCallback)
    fun stop()
}

internal interface LocationSourceCallback {
    fun onLatLngResult(latLng: LatLng)
}

