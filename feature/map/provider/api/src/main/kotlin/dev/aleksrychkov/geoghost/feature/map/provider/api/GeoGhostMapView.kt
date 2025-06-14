package dev.aleksrychkov.geoghost.feature.map.provider.api

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsHandler
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsListener

abstract class GeoGhostMapView(
    context: Context,
    private val lifecycleOwner: LifecycleOwner,
    protected val actionsListener: GeoGhostMapActionsListener,
) : FrameLayout(context),
    DefaultLifecycleObserver,
    ComponentCallbacks,
    GeoGhostMapActionsHandler {

    final override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleOwner.lifecycle.addObserver(this)
        (context.applicationContext as? Application)?.registerComponentCallbacks(this)
        onAttached()
    }

    final override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleOwner.lifecycle.removeObserver(this)
        (context.applicationContext as? Application)?.unregisterComponentCallbacks(this)
        onDetached()
    }

    abstract fun onAttached()
    abstract fun onDetached()

    final override fun onConfigurationChanged(newConfig: Configuration) {
        // do nothing
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
    }
}