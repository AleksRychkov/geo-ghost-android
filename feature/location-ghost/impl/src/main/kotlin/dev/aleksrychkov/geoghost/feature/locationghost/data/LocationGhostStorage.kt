package dev.aleksrychkov.geoghost.feature.locationghost.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.aleksrychkov.geoghost.core.model.LatLng
import kotlinx.coroutines.flow.firstOrNull

internal interface LocationGhostStorage {
    companion object {
        lateinit var instance: LocationGhostStorage
        fun initialize(context: Context) {
            instance = LocationGhostStorageDataStore(context)
        }
    }

    suspend fun getCurrentLocation(): LatLng?
    suspend fun setCurrentLocation(latLng: LatLng?)
    suspend fun inServiceRunning(): Boolean
    suspend fun setServiceRunning(value: Boolean)
}

private class LocationGhostStorageDataStore(
    private val context: Context,
) : LocationGhostStorage {

    private companion object {
        const val DATA_STORE_NAME = "location_ghost_store"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_SERVICE_STATE = "service_state"
    }

    private val Context._dataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> by lazy {
        context._dataStore
    }

    private val keyLatitude = doublePreferencesKey(KEY_LATITUDE)
    private val keyLongitude = doublePreferencesKey(KEY_LONGITUDE)
    private val keyServiceState = booleanPreferencesKey(KEY_SERVICE_STATE)

    override suspend fun getCurrentLocation(): LatLng? {
        val preferences = dataStore.data.firstOrNull() ?: return null
        val latitude: Double? = preferences[keyLatitude]
        val longitude: Double? = preferences[keyLongitude]
        return if (latitude == null && longitude == null) {
            null
        } else {
            LatLng(latitude = latitude!!, longitude = longitude!!)
        }
    }

    override suspend fun setCurrentLocation(latLng: LatLng?) {
        dataStore.edit { preferences ->
            if (latLng == null) {
                preferences.remove(keyLatitude)
                preferences.remove(keyLongitude)
            } else {
                preferences[keyLatitude] = latLng.latitude
                preferences[keyLongitude] = latLng.longitude
            }
        }
    }

    override suspend fun inServiceRunning(): Boolean {
        val preferences = dataStore.data.firstOrNull() ?: return false
        return preferences[keyServiceState] ?: false
    }

    override suspend fun setServiceRunning(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[keyServiceState] = value
        }
    }
}
