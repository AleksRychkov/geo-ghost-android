package dev.aleksrychkov.geoghost.feature.bookmark.ui.form

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.aleksrychkov.geoghost.bookmark.ui.R
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.bookmark.Bookmark
import dev.aleksrychkov.geoghost.feature.bookmark.BookmarkFactory
import dev.aleksrychkov.geoghost.feature.bookmark.exception.BookmarkAlreadyExistsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class BookmarkFormViewModel(
    private val dismiss: () -> Unit,
) : ViewModel() {

    private companion object {
        const val TAG = "BookmarkAddViewModel"
    }

    private val _state = MutableStateFlow(BookmarkFormState())
    val state = _state.asStateFlow()
    private lateinit var latLng: LatLng

    private val bookmark: Bookmark by lazy(mode = LazyThreadSafetyMode.NONE) {
        BookmarkFactory.instance.provideBookmark()
    }

    fun onNameInputChanged(value: String) {
        viewModelScope.launch {
            if (_state.value.entity != null) {
                return@launch
            }
            _state.emit(
                _state.value.copy(name = value)
            )
        }
    }

    fun addBookmark(context: Context) {
        if (_state.value.name.isBlank()) {
            Toast.makeText(
                context,
                context.getString(R.string.bookmark_screen_add_bookmark_empty_name),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        viewModelScope.launch {
            val name = _state.value.name
            bookmark.addBookmark(BookmarkEntity(name = name.trim(), latLng = latLng))
                .onSuccess {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.bookmark_screen_add_bookmark_success),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    dismiss()
                }
                .onFailure { exception ->
                    val failMessage = if (exception is BookmarkAlreadyExistsException) {
                        context.getString(R.string.bookmark_screen_add_bookmark_failure_already_exists)
                    } else {
                        dismiss()
                        Log.e(TAG, "Failed to create bookmark", exception)
                        context.getString(R.string.bookmark_screen_add_bookmark_failure)
                    }
                    Toast
                        .makeText(
                            context,
                            failMessage,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun deleteBookmark(context: Context) {
        val entity = _state.value.entity ?: return
        viewModelScope.launch {
            bookmark.removeBookmark(entity)
            dismiss()
        }
    }

    fun setLatLng(latLng: LatLng) {
        this.latLng = latLng
        viewModelScope.launch {
            bookmark.getByLatLng(latLng)
                .onSuccess { entity ->
                    _state.emit(
                        _state.value.copy(
                            name = entity.name,
                            location = entity.latLng.prettyPrint(),
                            entity = entity,
                        )
                    )
                }
                .onFailure {
                    println(it)
                    _state.emit(
                        _state.value.copy(
                            name = "",
                            location = latLng.prettyPrint(),
                            entity = null,
                        )
                    )
                }
        }
    }
}
