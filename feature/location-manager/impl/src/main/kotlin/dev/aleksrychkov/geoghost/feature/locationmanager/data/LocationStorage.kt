package dev.aleksrychkov.geoghost.feature.locationmanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.aleksrychkov.geoghost.core.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal interface LocationStorage {
    companion object {
        operator fun invoke(
            context: Context,
            dispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): LocationStorage = LocationStorageImpl(context = context, dispatcher = dispatcher)
    }

    suspend fun getLastKnownLatLng(): LatLng?
    suspend fun setLastKnownLatLng(latLng: LatLng)
}

private class LocationStorageImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : LocationStorage {

    private companion object {
        const val DATA_STORE_NAME = "location_store"
        const val KEY_LAST_KNOWN_LOCATION = "last_known_location"
    }

    private val Context._dataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> by lazy {
        context._dataStore
    }
    private val json: Json by lazy {
        Json { ignoreUnknownKeys = true }
    }
    private val key: Preferences.Key<String> by lazy {
        stringPreferencesKey(KEY_LAST_KNOWN_LOCATION)
    }

    override suspend fun getLastKnownLatLng(): LatLng? = withContext(dispatcher) {
        return@withContext dataStore.data
            .firstOrNull()
            ?.get(key)
            ?.let { latLngJson ->
                json.decodeFromString<LatLng>(latLngJson)
            }
    }

    override suspend fun setLastKnownLatLng(latLng: LatLng): Unit = withContext(dispatcher) {
        val latLngJson = json.encodeToString(latLng)
        dataStore.edit { preferences ->
            preferences[key] = latLngJson
        }
    }
}
