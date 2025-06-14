package dev.aleksrychkov.geoghost.feature.query.city

interface QueryCityFactory {
    companion object {
        lateinit var instance: QueryCityFactory
    }

    fun provideQueryCityUseCase(): QueryCityUseCase
}
