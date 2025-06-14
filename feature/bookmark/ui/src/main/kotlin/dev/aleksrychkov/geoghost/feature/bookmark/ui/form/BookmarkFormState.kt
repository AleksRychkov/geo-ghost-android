package dev.aleksrychkov.geoghost.feature.bookmark.ui.form

import androidx.compose.runtime.Immutable
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity

@Immutable
internal data class BookmarkFormState(
    val name: String = "",
    val location: String = "",
    val entity: BookmarkEntity? = null,
)
