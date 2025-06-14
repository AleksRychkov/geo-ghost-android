package dev.aleksrychkov.geoghost.feature.query.city.ui

import androidx.compose.runtime.Immutable
import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity

@Immutable
internal data class QueryCityState(
    val queryInProgress: Boolean = false,
    val query: String = "",
    val queryResult: List<CityEntity> = emptyList(),
    val showInternetError: Boolean = false,
    val showGeneralError: Boolean = false,
)
