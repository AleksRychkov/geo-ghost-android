package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal.initializer

import android.content.Context
import androidx.startup.Initializer
import org.maplibre.android.MapLibre

internal class LibreMapInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        MapLibre.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
