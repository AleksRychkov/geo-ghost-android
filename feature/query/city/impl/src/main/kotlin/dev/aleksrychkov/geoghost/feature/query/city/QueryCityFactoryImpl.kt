package dev.aleksrychkov.geoghost.feature.query.city

import kotlinx.coroutines.Dispatchers

internal class QueryCityFactoryImpl : QueryCityFactory {
    override fun provideQueryCityUseCase(): QueryCityUseCase {
        return QueryCityUseCaseImpl(
            dispatcher = Dispatchers.IO,
        )
    }
}
