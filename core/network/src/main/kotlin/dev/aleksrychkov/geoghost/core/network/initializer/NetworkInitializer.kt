package dev.aleksrychkov.geoghost.core.network.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.core.network.OkHttpClientProvider

class NetworkInitializer : Initializer<OkHttpClientProvider> {
    override fun create(context: Context): OkHttpClientProvider {
        OkHttpClientProvider.initialize(context)
        return OkHttpClientProvider.instance
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}