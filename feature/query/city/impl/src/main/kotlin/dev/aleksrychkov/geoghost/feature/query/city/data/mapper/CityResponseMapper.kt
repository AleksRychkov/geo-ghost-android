package dev.aleksrychkov.geoghost.feature.query.city.data.mapper

import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.query.city.data.entity.Binding
import dev.aleksrychkov.geoghost.feature.query.city.data.entity.CityResponse
import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity
import java.util.UUID

internal object CityResponseMapper {

    fun responseToApi(response: CityResponse): List<CityEntity> {
        val bindings = response.results?.bindings ?: return emptyList()
        return bindings
            .filterNotNull()
            .filter { binding: Binding ->
                binding.city?.value != null &&
                        binding.country?.value != null &&
                        binding.coordinates?.value != null
            }
            .map { binding: Binding ->
                val cityName = binding.city!!.value!!
                val countryName = binding.country!!.value!!
                val latLng = LatLng(
                    binding.coordinates!!.value!!.latitude,
                    binding.coordinates.value!!.longitude
                )
                CityEntity(
                    id = UUID.randomUUID().toString(),
                    name = cityName,
                    country = countryName,
                    latLng = latLng,
                )
            }
            .distinctBy {
                it.latLng
            }
    }
}
