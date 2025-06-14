package dev.aleksrychkov.geoghost.feature.bookmark.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.bookmark.ui.R
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal2X
import dev.aleksrychkov.geoghost.core.designsystem.theme.Tinny
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity

@Composable
fun BookmarkSelectScreen(
    modifier: Modifier = Modifier,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    BookmarkSelectScreenInner(
        modifier = modifier,
        onBookmarkClicked = onBookmarkClicked,
    )
}

@Composable
private fun BookmarkSelectScreenInner(
    modifier: Modifier,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    val vm: BookmarkListViewModel = viewModel { BookmarkListViewModel(onBookmarkClicked) }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.requestBookmarks()
    }

    DisposableEffect(vm) {
        onDispose {
            vm.submitQuery("")
        }
    }

    Content(
        modifier = modifier,
        state = state,
        submitQuery = vm::submitQuery,
        onBookmarkClicked = vm::onBookmarkCLicked,
        onDeleteBookmarkClicked = vm::onDeleteBookmarkClicked,
    )
}

@Composable
private fun Content(
    modifier: Modifier,
    state: BookmarkListState,
    submitQuery: (String) -> Unit,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
    onDeleteBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    Column(
        modifier = modifier.padding(Normal)
    ) {
        Query(
            modifier = Modifier.fillMaxWidth(),
            query = state.query,
            isQueryInProgress = state.isLoading,
            submitQuery = submitQuery,
        )

        if (state.query.isNotBlank()) {
            QueryResultContent(
                modifier = Modifier.fillMaxSize(),
                list = state.queryResult,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmarkClicked = onDeleteBookmarkClicked,
            )
        } else {
            ResultContent(
                modifier = Modifier.fillMaxSize(),
                list = state.bookmarks,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmarkClicked = onDeleteBookmarkClicked,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Query(
    modifier: Modifier,
    query: String,
    isQueryInProgress: Boolean,
    submitQuery: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = submitQuery,
                placeholder = { Text(stringResource(R.string.bookmark_screen_select_query_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    when {
                        isQueryInProgress -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(Normal2X)
                                    .clickable { submitQuery("") },
                                strokeWidth = Tinny,
                            )
                        }

                        query.isNotBlank() -> {
                            IconButton(onClick = { submitQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }

                        else -> {}
                    }
                },
                expanded = !isQueryInProgress,
                onSearch = { keyboardController?.hide() },
                onExpandedChange = {}
            )
        },
        expanded = false,
        onExpandedChange = {},
        windowInsets = WindowInsets(top = 0.dp),
        content = {}
    )
}

@Composable
private fun QueryResultContent(
    modifier: Modifier = Modifier,
    list: List<BookmarkEntity>,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
    onDeleteBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    when {
        list.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.bookmark_screen_select_query_empty))
            }
        }

        else -> {
            Bookmarks(
                modifier = Modifier.fillMaxSize(),
                list = list,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmarkClicked = onDeleteBookmarkClicked,
            )
        }
    }
}

@Composable
private fun ResultContent(
    modifier: Modifier = Modifier,
    list: List<BookmarkEntity>,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
    onDeleteBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    when {
        list.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.bookmark_screen_select_empty))
            }
        }

        else -> {
            Bookmarks(
                modifier = Modifier.fillMaxSize(),
                list = list,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmarkClicked = onDeleteBookmarkClicked,
            )
        }
    }
}

@Composable
private fun Bookmarks(
    modifier: Modifier = Modifier,
    list: List<BookmarkEntity>,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
    onDeleteBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    val listState = rememberLazyListState()
    val hideKeyboard by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    val localFocusManager = LocalFocusManager.current
    LaunchedEffect(hideKeyboard) {
        if (hideKeyboard) {
            localFocusManager.clearFocus(force = true)
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(vertical = Normal),
        verticalArrangement = Arrangement.spacedBy(Normal),
    ) {
        items(
            items = list,
            key = { item -> item.name }
        ) { item ->
            BookmarkItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                entity = item,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmarkClicked = onDeleteBookmarkClicked,
            )
        }
    }
}

@Composable
private fun BookmarkItem(
    modifier: Modifier = Modifier,
    entity: BookmarkEntity,
    onBookmarkClicked: (BookmarkEntity) -> Unit,
    onDeleteBookmarkClicked: (BookmarkEntity) -> Unit,
) {
    Card(
        modifier = modifier
            .clip(CardDefaults.shape)
            .clickable {
                onBookmarkClicked(entity)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Normal)
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text(
                    text = entity.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = entity.latLng.prettyPrint(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            IconButton(
                onClick = {
                    onDeleteBookmarkClicked(entity)
                }
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.bookmark_screen_remove_bookmark)
                )
            }
        }
    }
}