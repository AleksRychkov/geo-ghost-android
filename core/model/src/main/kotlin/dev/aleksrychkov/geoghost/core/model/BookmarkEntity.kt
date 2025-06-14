package dev.aleksrychkov.geoghost.core.model

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkEntity(
    val name: String,
    val latLng: LatLng,
)
