@file:Suppress("DEPRECATION")

package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

internal class LibreAnimatedMarker(
    private val context: Context,
    map: MapLibreMap,
    latLng: LatLng,
    private val scope: CoroutineScope,
    private val framesDelay: Long = 16,
    @DrawableRes private val frames: Array<Int>,
) {

    private var wasStopped: Boolean = false
    private var marker: Marker? = null

    private val bitmaps = mutableListOf<Bitmap>()
    private val icons = mutableListOf<Icon>()
    private var animationIndex: Int = 0
    private var animationJob: Job? = null

    init {
        val markerOptions = MarkerOptions().position(latLng)
        animationJob = scope.launch {
            withContext(Dispatchers.Default) {
                buildBitmaps()
                buildIcons()
            }
            marker = map.addMarker(markerOptions)
            marker?.icon = icons[animationIndex]
            animationIndex = (animationIndex + 1) % frames.size
            animate()
        }
    }

    fun onStart() {
        if (!wasStopped) return
        animationJob = scope.launch {
            animate()
        }
    }

    fun onStop() {
        wasStopped = true
        animationJob?.cancel()
        animationJob = null
    }

    fun onDestroy() {
        animationJob?.cancel()
        animationJob = null
        bitmaps.forEach { it.recycle() }
        marker?.remove()
        marker = null
    }

    fun updateLatLng(latLng: LatLng) {
        marker?.position = latLng
    }

    fun id(): Long? {
        return marker?.id
    }

    private fun buildBitmaps() {
        frames.forEach { resId ->
            val drawable: Drawable = ContextCompat.getDrawable(context, resId)!!
            val bitmap =
                createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmaps.add(bitmap)
        }
    }

    private fun buildIcons() {
        bitmaps
            .map { bmp ->
                IconFactory
                    .getInstance(context)
                    .fromBitmap(bmp)
            }
            .let(icons::addAll)
    }

    private suspend fun animate() {
        if (frames.size <= 1) return
        do {
            delay(framesDelay)
            // update frame
            marker?.icon = icons[animationIndex]
            animationIndex = (animationIndex + 1) % frames.size
        } while (animationJob?.isActive == true)
    }
}


internal object LibreAnimatedMarkerFactory {
    fun tmpMarker(
        context: Context,
        map: MapLibreMap,
        latLng: LatLng,
        scope: CoroutineScope,
    ): LibreAnimatedMarker {
        return LibreAnimatedMarker(
            context = context,
            map = map,
            latLng = latLng,
            scope = scope,
            framesDelay = 100,
            frames = arrayOf(
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_1,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_2,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_3,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_4,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_5,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_6,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_7,
                dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_tmp_marker_8,
            ),
        )
    }

    fun ghostMarker(
        context: Context,
        map: MapLibreMap,
        latLng: LatLng,
        scope: CoroutineScope,
    ): LibreAnimatedMarker {
        return LibreAnimatedMarker(
            context = context,
            map = map,
            latLng = latLng,
            scope = scope,
            frames = arrayOf(dev.aleksrychkov.geoghost.feature.map.provider.api.R.drawable.ic_ghost_marker),
        )
    }
}
