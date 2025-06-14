package dev.aleksrychkov.geoghost.feature.bookmark

import dev.aleksrychkov.geoghost.core.exception.safeRunCatching
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.bookmark.data.BookmarkStorage
import dev.aleksrychkov.geoghost.feature.bookmark.exception.BookmarkAlreadyExistsException
import dev.aleksrychkov.geoghost.feature.bookmark.exception.BookmarkNotFoundException

internal class BookmarkImpl(
    private val bookmarkStorage: BookmarkStorage
) : Bookmark {

    override suspend fun bookmarks(): Result<List<BookmarkEntity>> {
        return safeRunCatching {
            bookmarkStorage.getBookmarks()
        }
    }

    override suspend fun addBookmark(entity: BookmarkEntity): Result<Unit> {
        return safeRunCatching {
            val bookmark = bookmarkStorage.getBookmark(entity.name)
            if (bookmark != null) {
                throw BookmarkAlreadyExistsException(entity.name)
            }
            bookmarkStorage.addBookmark(entity)
        }
    }

    override suspend fun removeBookmark(entity: BookmarkEntity): Result<Unit> {
        return safeRunCatching {
            bookmarkStorage.removeBookmark(entity)
        }
    }

    override suspend fun getByLatLng(latLng: LatLng): Result<BookmarkEntity> {
        return safeRunCatching {
            bookmarkStorage.getByLatLng(latLng) ?: throw BookmarkNotFoundException()
        }
    }
}
