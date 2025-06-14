package dev.aleksrychkov.geoghost.feature.query.city.data.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class CityResponse(
    @SerialName("results") val results: Bindings? = null
)

@Serializable
internal data class Bindings(
    @SerialName("bindings") val bindings: List<Binding?>? = null
)

@Serializable
internal data class Binding(
    @SerialName("coordinates") val coordinates: LiteralCoordinates? = null,
    @SerialName("cityLabel") val city: Literal? = null,
    @SerialName("countryLabel") val country: Literal? = null,
)

@Serializable
internal data class Literal(
    @SerialName("value") val value: String?
)

@Serializable
internal data class LiteralCoordinates(
    @SerialName("value") val value: Coordinates?
)

@Serializable(with = CoordinatesSerializer::class)
internal data class Coordinates(val longitude: Double, val latitude: Double)

private object CoordinatesSerializer : KSerializer<Coordinates?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Coordinates", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Coordinates? {
        val value = decoder.decodeString()
        val regex = Regex("""Point\(([-\d.]+) ([-\d.]+)\)""")
        val matchResult = regex.matchEntire(value) ?: return null
        val (lon, lat) = matchResult.destructured
        return Coordinates(lon.toDouble(), lat.toDouble())
    }

    override fun serialize(encoder: Encoder, value: Coordinates?) {
        val stringValue = if (value == null) {
            "Point( )"
        } else {
            "Point(${value.longitude} ${value.latitude})"
        }
        encoder.encodeString(stringValue)
    }
}