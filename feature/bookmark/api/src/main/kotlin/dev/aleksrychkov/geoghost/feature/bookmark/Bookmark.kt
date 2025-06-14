package dev.aleksrychkov.geoghost.feature.bookmark

import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng

interface Bookmark {
    suspend fun bookmarks(): Result<List<BookmarkEntity>>
    suspend fun removeBookmark(entity: BookmarkEntity): Result<Unit>
    suspend fun addBookmark(entity: BookmarkEntity): Result<Unit>
    suspend fun getByLatLng(latLng: LatLng): Result<BookmarkEntity>
}
