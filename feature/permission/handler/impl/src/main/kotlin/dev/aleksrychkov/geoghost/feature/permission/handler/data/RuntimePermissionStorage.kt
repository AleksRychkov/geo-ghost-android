package dev.aleksrychkov.geoghost.feature.permission.handler.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

internal interface RuntimePermissionStorage {

    companion object {
        lateinit var instance: RuntimePermissionStorage
        fun initialize(context: Context) {
            instance = RuntimePermissionStorageDataStore(context)
        }
    }

    suspend fun getShouldShowRational(permission: String): Boolean
    suspend fun setShouldShowRational(permission: String, value: Boolean)
}

private class RuntimePermissionStorageDataStore(
    private val context: Context,
) : RuntimePermissionStorage {

    private companion object {
        const val DATA_STORE_NAME = "runtime_permission_store"
    }

    private val Context._dataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> by lazy { context._dataStore }

    override suspend fun getShouldShowRational(permission: String): Boolean {
        val key = booleanPreferencesKey(permission)
        val result = dataStore.data.map { preferences ->
            preferences[key] ?: false
        }.firstOrNull() ?: false
        return result
    }

    override suspend fun setShouldShowRational(permission: String, value: Boolean) {
        val key = booleanPreferencesKey(permission)
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
