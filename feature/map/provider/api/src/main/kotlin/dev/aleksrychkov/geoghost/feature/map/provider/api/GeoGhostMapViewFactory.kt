package dev.aleksrychkov.geoghost.feature.map.provider.api

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsListener
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapView

interface GeoGhostMapViewFactory {
    fun provide(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        paddingTop: Int,
        paddingEnd: Int,
        geoGhostMapActionsListener: GeoGhostMapActionsListener,
    ): GeoGhostMapView
}
