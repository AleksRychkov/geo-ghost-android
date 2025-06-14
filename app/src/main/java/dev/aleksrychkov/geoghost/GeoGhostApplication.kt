package dev.aleksrychkov.geoghost

import android.app.Application
import dev.aleksrychkov.geoghost.core.buildconfig.BuildConfigProxy

class GeoGhostApplication : Application() {

    companion object {
        init {
            BuildConfigProxy.DEBUG = BuildConfig.DEBUG
        }
    }
}
