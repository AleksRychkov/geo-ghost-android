package dev.aleksrychkov.geoghost.feature.query.city.model

import dev.aleksrychkov.geoghost.core.model.LatLng

data class CityEntity(
    val id: String,
    val name: String,
    val country: String,
    val latLng: LatLng,
)
