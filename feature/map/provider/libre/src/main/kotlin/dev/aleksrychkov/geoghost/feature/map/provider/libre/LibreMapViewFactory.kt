package dev.aleksrychkov.geoghost.feature.map.provider.libre

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsListener
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapView
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapViewFactory
import dev.aleksrychkov.geoghost.feature.map.provider.libre.internal.LibreMapView

object LibreMapViewFactory : GeoGhostMapViewFactory {
    override fun provide(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        paddingTop: Int,
        paddingEnd: Int,
        geoGhostMapActionsListener: GeoGhostMapActionsListener,
    ): GeoGhostMapView {
        return LibreMapView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            actionsListener = geoGhostMapActionsListener,
            paddingTop = paddingTop,
            paddingEnd = paddingEnd,
        )
    }
}
