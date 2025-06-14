package dev.aleksrychkov.geoghost.feature.query.city

import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity

interface QueryCityUseCase {
    suspend fun queryCity(input: String): Result<List<CityEntity>>
}
