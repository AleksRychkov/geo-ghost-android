package dev.aleksrychkov.geoghost.feature.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsHandler
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsListener
import dev.aleksrychkov.geoghost.feature.map.provider.libre.LibreMapViewFactory

@Composable
internal fun MapWrapper(
    modifier: Modifier = Modifier,
    paddingTop: Int,
    paddingEnd: Int,
    attachMapHandler: (GeoGhostMapActionsHandler) -> Unit,
    detachMapHandler: () -> Unit,
    onMapActionTmpLocationSet: (LatLng) -> Unit,
    onMapReady: () -> Unit,
    onTmpMarkerClick: () -> Unit,
    onGhostMarkerClick: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LibreMapViewFactory.provide(
                context = context,
                lifecycleOwner = lifecycleOwner,
                paddingTop = paddingTop,
                paddingEnd = paddingEnd,
                object : GeoGhostMapActionsListener {
                    override fun setTmpLocation(location: LatLng) {
                        onMapActionTmpLocationSet(location)
                    }

                    override fun mapIsReady() {
                        onMapReady()
                    }

                    override fun onTmpMarkerClick() {
                        onTmpMarkerClick()
                    }

                    override fun onGhostMarkerClick() {
                        onGhostMarkerClick()
                    }
                })
        },
        update = { view ->
            attachMapHandler(view as GeoGhostMapActionsHandler)
        },
        onRelease = {
            detachMapHandler()
        }
    )
}
