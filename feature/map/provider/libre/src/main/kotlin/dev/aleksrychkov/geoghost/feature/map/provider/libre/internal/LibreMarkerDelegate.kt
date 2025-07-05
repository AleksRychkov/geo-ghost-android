@file:Suppress("DEPRECATION")

package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import org.maplibre.android.annotations.Marker
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

internal class LibreMarkerDelegate(
    lifecycleOwner: LifecycleOwner,
    private val onTmpMarkerClicked: () -> Unit,
    private val onGhostMarkerClicked: () -> Unit,
) : MapLibreMap.OnMarkerClickListener {

    private val scope: CoroutineScope
    private var tmpMarker: LibreAnimatedMarker? = null
    private var ghostMarker: LibreAnimatedMarker? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            tmpMarker?.onStart()
            ghostMarker?.onStart()
        }

        override fun onStop(owner: LifecycleOwner) {
            tmpMarker?.onStop()
            ghostMarker?.onStop()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            removeTmpMarker()
            removeGhostMarker()
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        scope = lifecycleOwner.lifecycleScope
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.id == tmpMarker?.id()) {
            onTmpMarkerClicked()
            return true
        }
        if (marker.id == ghostMarker?.id()) {
            onGhostMarkerClicked()
            return true
        }
        return false
    }

    fun removeTmpMarker() {
        tmpMarker?.onDestroy()
        tmpMarker = null
    }

    fun removeGhostMarker() {
        ghostMarker?.onDestroy()
        ghostMarker = null
    }

    fun setTmpMarker(
        context: Context,
        map: MapLibreMap,
        latLng: LatLng,
    ) {
        if (tmpMarker != null) {
            tmpMarker?.updateLatLng(latLng)
        } else {
            tmpMarker = LibreAnimatedMarkerFactory.tmpMarker(
                context = context,
                map = map,
                latLng = latLng,
                scope = scope,
            )
        }
    }

    fun setGhostMarker(
        context: Context,
        map: MapLibreMap,
        latLng: LatLng,
    ) {
        if (ghostMarker != null) {
            ghostMarker?.updateLatLng(latLng)
        } else {
            ghostMarker = LibreAnimatedMarkerFactory.ghostMarker(
                context = context,
                map = map,
                latLng = latLng,
                scope = scope,
            )
        }
    }
}
