package dev.aleksrychkov.geoghost.feature.permission.handler.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.feature.permission.handler.RuntimePermissionActivityHandler
import dev.aleksrychkov.geoghost.feature.permission.handler.data.RuntimePermissionStorage

@Suppress("unused")
internal class RuntimePermissionInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        RuntimePermissionStorage.initialize(context)
        RuntimePermissionActivityHandler(context)
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
