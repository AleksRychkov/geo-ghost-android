package dev.aleksrychkov.geoghost.feature.query.city.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.core.network.initializer.NetworkInitializer
import dev.aleksrychkov.geoghost.feature.query.city.QueryCityFactory
import dev.aleksrychkov.geoghost.feature.query.city.QueryCityFactoryImpl

@Suppress("unused")
internal class QueryCityInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        QueryCityFactory.instance = QueryCityFactoryImpl()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(NetworkInitializer::class.java)
    }
}
