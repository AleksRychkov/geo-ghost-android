package dev.aleksrychkov.geoghost.feature.bookmark.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.aleksrychkov.geoghost.core.exception.NoStackTraceException
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal interface BookmarkStorage {
    companion object {
        operator fun invoke(
            context: Context,
            dispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): BookmarkStorage = BookmarkStorageDataStore(context = context, dispatcher = dispatcher)
    }

    suspend fun getBookmarks(): List<BookmarkEntity>
    suspend fun getByLatLng(latLng: LatLng): BookmarkEntity?

    @Throws(EmptyNameException::class)
    suspend fun getBookmark(name: String): BookmarkEntity?

    @Throws(EmptyNameException::class)
    suspend fun addBookmark(entity: BookmarkEntity)

    @Throws(EmptyNameException::class)
    suspend fun removeBookmark(entity: BookmarkEntity)
}

internal class EmptyNameException : NoStackTraceException()

private class BookmarkStorageDataStore(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : BookmarkStorage {

    private companion object {
        const val DATA_STORE_NAME = "bookmark_store"
    }

    private val Context._dataStore by preferencesDataStore(DATA_STORE_NAME)
    private val dataStore: DataStore<Preferences> by lazy {
        context._dataStore
    }
    private val json: Json by lazy {
        Json { ignoreUnknownKeys = true }
    }

    private val runtimeCacheMutex: Mutex by lazy { Mutex() }
    private var isFirstReadCompleted: Boolean = false
    private val _runtimeCache: MutableList<BookmarkEntity> by lazy { mutableListOf() }

    override suspend fun getBookmarks(): List<BookmarkEntity> {
        return runtimeCache { toList() }
    }

    override suspend fun addBookmark(entity: BookmarkEntity) {
        if (entity.name.isBlank()) throw EmptyNameException()
        runtimeCache {
            add(entity)
        }
        val key = stringPreferencesKey(entity.name)
        val json = json.encodeToString(entity)
        dataStore.edit { preferences ->
            preferences[key] = json
        }
    }

    override suspend fun removeBookmark(entity: BookmarkEntity) {
        if (entity.name.isBlank()) throw EmptyNameException()
        runtimeCache {
            remove(entity)
        }
        val key = stringPreferencesKey(entity.name)
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    override suspend fun getBookmark(name: String): BookmarkEntity? {
        if (name.isBlank()) throw EmptyNameException()
        return runtimeCache {
            firstOrNull { it.name == name }
        }
    }

    override suspend fun getByLatLng(latLng: LatLng): BookmarkEntity? {
        return runtimeCache {
            firstOrNull { it.latLng == latLng }
        }
    }

    private suspend fun <T> runtimeCache(block: MutableList<BookmarkEntity>.() -> T): T {
        runtimeCacheMutex.withLock {
            makeFirstReadIfNeeded()
            return block(_runtimeCache)
        }
    }

    private suspend fun makeFirstReadIfNeeded(): Unit = withContext(dispatcher) {
        if (isFirstReadCompleted) return@withContext
        isFirstReadCompleted = true
        dataStore.data
            .map { preferences ->
                preferences.asMap().values
                    .mapNotNull { it as? String }
                    .map { json.decodeFromString<BookmarkEntity>(it) }
                    .let(_runtimeCache::addAll)
            }
            .firstOrNull()
    }
}