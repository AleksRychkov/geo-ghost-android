package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsListener
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapView
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import dev.aleksrychkov.geoghost.core.model.LatLng as ApiLatLng


@Suppress("DEPRECATION")
@SuppressLint("ViewConstructor")
internal class LibreMapView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    actionsListener: GeoGhostMapActionsListener,
    private val paddingTop: Int,
    private val paddingEnd: Int,
) : GeoGhostMapView(context, lifecycleOwner, actionsListener),
    OnMapReadyCallback,
    MapLibreMap.OnMapClickListener {

    private companion object {
        const val TILES_SERVER = "https://tiles.openfreemap.org/styles/liberty"
        const val TILES_SERVER_DARK = "https://tiles.openfreemap.org/styles/dark"
        const val DEFAULT_CAMERA_ZOOM = 12.0
    }

    private var mapView: MapView? = null
    private var mapLibre: MapLibreMap? = null

    private val markerDelegate = LibreMarkerDelegate(
        lifecycleOwner = lifecycleOwner,
        onTmpMarkerClicked = actionsListener::onTmpMarkerClick,
        onGhostMarkerClicked = actionsListener::onGhostMarkerClick,
    )
    private val locationMarkerDelegate = LibreCurrentLocationDelegate(
        lifecycleOwner = lifecycleOwner,
        context = context,
    )

    private var isCurrentUiModeDark: Boolean = false
    private var loaderView: LibreMapLoaderView? = null

    // region mapLibre callbacks
    override fun onMapReady(map: MapLibreMap) {
        mapLibre = map
        val tilesServer = if (isDarkMode()) {
            TILES_SERVER_DARK
        } else {
            TILES_SERVER
        }
        map.setStyle(tilesServer) {
            moveCameraToCurrentUserLocation()

            loaderView?.removeWithAnimation(removeFromLayout = ::removeView)

            map.uiSettings.setCompassMargins(0, paddingTop, paddingEnd, 0)
            map.addOnMapClickListener(this)
            map.setOnMarkerClickListener(markerDelegate)
            actionsListener.mapIsReady()
            locationMarkerDelegate.libreMap = map
        }
    }

    override fun onMapClick(latLng: LatLng): Boolean {
        val map = mapLibre ?: return false

        markerDelegate.setTmpMarker(context = context, map = map, latLng = latLng)

        actionsListener.setTmpLocation(ApiLatLng(latLng.latitude, latLng.longitude))
        return true
    }
    // endregion mapLibre callbacks

    // region GeoGhostMapView callbacks
    @SuppressLint("MissingPermission")
    override fun onAttached() {
        isCurrentUiModeDark = isDarkMode()
        mapView = MapView(context)
        mapView?.getMapAsync(this)
        addView(
            mapView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        loaderView = LibreMapLoaderView(context)
        addView(
            loaderView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onDetached() {
        mapView?.onDestroy()
        mapLibre?.removeOnMapClickListener(this)
        mapLibre?.setOnMarkerClickListener(null)
        mapView = null
        mapLibre = null
        locationMarkerDelegate.libreMap = null
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        mapView?.onLowMemory()
    }

    override fun onStart(owner: LifecycleOwner) {
        mapView?.onStart()
    }

    override fun onStop(owner: LifecycleOwner) {
        mapView?.onStop()
    }

    override fun onResume(owner: LifecycleOwner) {
        mapView?.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        mapView?.onPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mapView?.onDestroy()
    }

    override fun moveCameraToCurrentUserLocation() {
        mapLibre ?: return
        val latLng = locationMarkerDelegate.lastKnownLocation ?: defaultStatLatLng.random()
        moveCameraToLocation(latLng)
    }

    override fun moveCameraToLatLng(latLng: ApiLatLng) {
        mapLibre ?: return
        moveCameraToLocation(LatLng(latitude = latLng.latitude, longitude = latLng.longitude))
    }

    override fun removeTmpMarker() {
        markerDelegate.removeTmpMarker()
    }

    override fun setTmpLocation(latLng: ApiLatLng) {
        val map = mapLibre ?: return
        markerDelegate.setTmpMarker(
            context = context,
            map = map,
            latLng = LatLng(latitude = latLng.latitude, longitude = latLng.longitude)
        )
        actionsListener.setTmpLocation(ApiLatLng(latLng.latitude, latLng.longitude))
    }

    override fun removeGhostMarker() {
        markerDelegate.removeGhostMarker()
    }

    override fun setGhostLocation(latLng: ApiLatLng) {
        val map = mapLibre ?: return
        val location = LatLng(latLng.latitude, latLng.longitude)
        markerDelegate.setGhostMarker(
            context = context,
            map = map,
            latLng = location,
        )
        moveCameraToLocation(location)
    }
    // endregion GeoGhostMapView callbacks

    private fun moveCameraToLocation(latLng: LatLng) {
        val map = mapLibre ?: return
        // todo: right after map is initialized animateCamera sometimes doesn't trigger action
        postDelayed({
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    DEFAULT_CAMERA_ZOOM
                )
            )
        }, 250)

    }

    private fun isDarkMode(): Boolean {
        val darkModeFlag =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }
}
