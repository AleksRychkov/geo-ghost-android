package dev.aleksrychkov.geoghost.feature.query.city

import dev.aleksrychkov.geoghost.core.exception.safeRunCatching
import dev.aleksrychkov.geoghost.core.network.executeForResult
import dev.aleksrychkov.geoghost.feature.query.city.data.entity.CityResponse
import dev.aleksrychkov.geoghost.feature.query.city.data.mapper.CityResponseMapper
import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.Request

internal class QueryCityUseCaseImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : QueryCityUseCase {

    override suspend fun queryCity(input: String): Result<List<CityEntity>> =
        withContext(dispatcher) {
            val request = request(input)

            val result = request.executeForResult<CityResponse>()
            if (result.isSuccess) {
                safeRunCatching {
                    CityResponseMapper.responseToApi(result.getOrThrow())
                }
            } else {
                result
                    .exceptionOrNull()
                    ?.let { Result.failure(it) }
                    ?: Result.success(emptyList())
            }
        }

    private fun request(input: String): Request {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("query.wikidata.org")
            .addPathSegment("sparql")
            .addQueryParameter("query", query(input.trim())).build()
        return Request
            .Builder()
            .addHeader("Accept", "application/sparql-results+json")
            .url(url)
            .build()
    }

    private fun query(input: String): String {
        return """
        SELECT ?cityLabel ?coordinates ?countryLabel WHERE {
          ?city rdfs:label ?cityLabel ;
                wdt:P31/wdt:P279* wd:Q515 ;
                wdt:P625 ?coordinates ;
                wdt:P17 ?country .
          FILTER(STRSTARTS(LCASE(?cityLabel), "${input.lowercase()}"))
          FILTER(LANG(?cityLabel) = "en")
          SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
        }
        LIMIT 10
    """.trimIndent()
    }
}
