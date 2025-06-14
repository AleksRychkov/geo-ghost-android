package dev.aleksrychkov.geoghost.feature.bookmark.exception

import dev.aleksrychkov.geoghost.core.exception.NoStackTraceException

class BookmarkAlreadyExistsException(name: String) :
    NoStackTraceException("Bookmark with name: $name already exists")