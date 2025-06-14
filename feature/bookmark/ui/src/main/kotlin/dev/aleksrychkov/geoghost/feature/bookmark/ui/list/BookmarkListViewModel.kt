package dev.aleksrychkov.geoghost.feature.bookmark.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.feature.bookmark.Bookmark
import dev.aleksrychkov.geoghost.feature.bookmark.BookmarkFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
internal class BookmarkListViewModel(
    private val onBookmarkClicked: (BookmarkEntity) -> Unit,
) : ViewModel() {
    private companion object {
        const val TAG = "BookmarkListViewModel"
        const val DEBOUNCE_DELAY_MILLIS = 100L
    }

    private val _state = MutableStateFlow(BookmarkListState())
    val state = _state.asStateFlow()

    private val queryFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val bookmark: Bookmark by lazy {
        BookmarkFactory.instance.provideBookmark()
    }

    init {
        queryFlow
            .flatMapLatest { value ->
                if (value.isBlank()) {
                    flowOf(value)
                } else {
                    flowOf(value).debounce(DEBOUNCE_DELAY_MILLIS.milliseconds)
                }
            }
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    val res = _state.value.bookmarks.filter {
                        it.name.lowercase().contains(query.lowercase())
                    }
                    flowOf(res)
                }
            }
            .catch { e ->
                Log.e(TAG, "Failed to query city", e)
                emit(emptyList())
            }
            .onEach { result ->
                _state.emit(_state.value.copy(queryResult = result))
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun requestBookmarks() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isLoading = true))

            bookmark.bookmarks()
                .onSuccess {
                    val res = _state.value.copy(bookmarks = it, isLoading = false)
                    _state.emit(res)
                }
                .onFailure {
                    val res = _state.value.copy(bookmarks = emptyList(), isLoading = false)
                    _state.emit(res)
                }
        }
    }

    fun submitQuery(query: String) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(query = query))
            queryFlow.emit(query)
        }
    }

    fun onBookmarkCLicked(entity: BookmarkEntity) {
        onBookmarkClicked(entity)
    }

    fun onDeleteBookmarkClicked(entity: BookmarkEntity) {
        viewModelScope.launch {
            val bookmarks = _state.value.bookmarks.filter { it != entity }
            _state.emit(_state.value.copy(bookmarks = bookmarks))
            queryFlow.emit(_state.value.query)

            bookmark.removeBookmark(entity)
        }
    }
}
