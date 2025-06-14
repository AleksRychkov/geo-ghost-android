package dev.aleksrychkov.geoghost.feature.bookmark.initializer

import android.content.Context
import androidx.startup.Initializer
import dev.aleksrychkov.geoghost.feature.bookmark.BookmarkFactory
import dev.aleksrychkov.geoghost.feature.bookmark.BookmarkFactoryImpl
import dev.aleksrychkov.geoghost.feature.bookmark.data.BookmarkStorage

@Suppress("unused")
internal class BookmarkInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val storage = BookmarkStorage(context)
        BookmarkFactory.instance = BookmarkFactoryImpl(storage)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
