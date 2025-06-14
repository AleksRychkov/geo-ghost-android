package dev.aleksrychkov.geoghost.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double,
) {
    fun prettyPrint(
        prefixLatitude: String = "lat: ",
        prefixLongitude: String = "lng: "
    ): String {
        return "$prefixLatitude$latitude\n$prefixLongitude$longitude"
    }
}

