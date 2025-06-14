package dev.aleksrychkov.geoghost.feature.bookmark

interface BookmarkFactory {
    companion object {
        lateinit var instance: BookmarkFactory
    }

    fun provideBookmark(): Bookmark
}
