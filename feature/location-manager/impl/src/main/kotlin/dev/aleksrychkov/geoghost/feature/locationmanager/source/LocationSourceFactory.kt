package dev.aleksrychkov.geoghost.feature.locationmanager.source

import android.content.Context

internal interface LocationSourceFactory {
    companion object {
        operator fun invoke(context: Context): LocationSourceFactory =
            LocationSourceFactoryImpl(context)
    }

    fun fusedLocationSource(): LocationSource
}

private class LocationSourceFactoryImpl(private val context: Context) : LocationSourceFactory {
    private val fusedLock = Any()
    private var fusedSource: LocationSource? = null
    override fun fusedLocationSource(): LocationSource {
        synchronized(fusedLock) {
            if (fusedSource == null) {
                fusedSource = FusedLocationSource(context)
            }
        }
        return requireNotNull(fusedSource)
    }
}
