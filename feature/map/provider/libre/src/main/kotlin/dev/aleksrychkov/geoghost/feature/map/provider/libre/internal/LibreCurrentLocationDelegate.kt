@file:Suppress("DEPRECATION")

package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.aleksrychkov.geoghost.feature.locationmanager.LocationManager
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import dev.aleksrychkov.geoghost.core.model.LatLng as ApiLatLng

internal class LibreCurrentLocationDelegate(
    lifecycleOwner: LifecycleOwner,
    private val context: Context,
) {

    var libreMap: MapLibreMap? = null
    var lastKnownLocation: LatLng? = null

    private var locationMarker: Marker? = null

    init {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                LocationManager.instance.location().collect {
                    updateLocationMarker(it)
                }
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            LocationManager.instance.lastKnownLocation()?.let {
                lastKnownLocation = LatLng(latitude = it.latitude, longitude = it.longitude)
            }
        }
    }

    private fun updateLocationMarker(latLng: ApiLatLng) {
        val position = LatLng(
            latitude = latLng.latitude,
            longitude = latLng.longitude
        )
        lastKnownLocation = position
        val map = libreMap ?: return
        val marker = marker(map, position) ?: return
        marker.position = position
    }

    private fun marker(
        map: MapLibreMap,
        position: LatLng,
    ): Marker? {
        if (locationMarker == null) {
            val markerOptions = MarkerOptions().apply {
                val drawable: Drawable = ContextCompat.getDrawable(
                    context,
                    dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_gps_location
                )!!
                val bitmap =
                    createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                this.icon = IconFactory
                    .getInstance(context)
                    .fromBitmap(bitmap)
                this.position = position
            }
            locationMarker = map.addMarker(markerOptions)
        }
        return locationMarker
    }
}
