package dev.aleksrychkov.geoghost.feature.bookmark.ui.list

import androidx.compose.runtime.Immutable
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity

@Immutable
internal data class BookmarkListState(
    val isLoading: Boolean = false,
    val query: String = "",
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val queryResult: List<BookmarkEntity> = emptyList(),
)