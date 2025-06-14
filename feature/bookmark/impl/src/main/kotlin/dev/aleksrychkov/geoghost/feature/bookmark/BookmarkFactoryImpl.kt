package dev.aleksrychkov.geoghost.feature.bookmark

import dev.aleksrychkov.geoghost.feature.bookmark.data.BookmarkStorage

internal class BookmarkFactoryImpl(
    private val storage: BookmarkStorage
) : BookmarkFactory {
    override fun provideBookmark(): Bookmark = BookmarkImpl(storage)
}
