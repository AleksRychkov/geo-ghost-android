package dev.aleksrychkov.geoghost.feature.query.city.ui

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.core.designsystem.theme.HalfNormal
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal2X
import dev.aleksrychkov.geoghost.core.designsystem.theme.Small
import dev.aleksrychkov.geoghost.core.designsystem.theme.Tinny
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.query.city.model.CityEntity

@Composable
fun QueryCityScreen(
    modifier: Modifier,
    onCityLatLngClicked: (LatLng) -> Unit,
) {
    QueryCityScreenInner(
        modifier = modifier,
        onCityLatLngClicked = onCityLatLngClicked,
    )
}

@Composable
private fun QueryCityScreenInner(
    modifier: Modifier = Modifier,
    onCityLatLngClicked: (LatLng) -> Unit,
) {
    val vm: QueryCityViewModel = viewModel { QueryCityViewModel(onCityLatLngClicked) }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.startQueryFlow()
    }
    DisposableEffect(vm) {
        onDispose { vm.submitQuery("") }
    }
    val activity = LocalActivity.current
    DisposableEffect(vm) {
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Content(
        modifier = modifier,
        state = state,
        submitQuery = vm::submitQuery,
        onCityClick = vm::onCityClicked,
        onRetryClicked = vm::onRetryClicked,
    )
}

@Composable
private fun Content(
    modifier: Modifier,
    state: QueryCityState,
    submitQuery: (String) -> Unit,
    onCityClick: (CityEntity) -> Unit,
    onRetryClicked: () -> Unit,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Normal)
        ) {
            Query(
                modifier = Modifier.fillMaxWidth(),
                query = state.query,
                isQueryInProgress = state.queryInProgress,
                submitQuery = submitQuery,
            )

            QueryResultContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = Normal),
                state = state,
                onCityClick = onCityClick,
            )
        }

        ErrorContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            state = state,
            onRetryClicked = onRetryClicked,
        )
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
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = submitQuery,
                placeholder = { Text(stringResource(R.string.query_city_screen_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    when {
                        isQueryInProgress -> {
                            IconButton(onClick = { submitQuery("") }) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(Normal2X),
                                    strokeWidth = Tinny,
                                )
                            }
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
                expanded = false,
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
    state: QueryCityState,
    onCityClick: (CityEntity) -> Unit,
) {
    when {
        state.queryInProgress -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.query_city_screen_search_in_progress))
            }
        }

        state.queryResult.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val text = if (state.query.isBlank()) {
                    stringResource(R.string.query_city_screen_search_hint)
                } else {
                    stringResource(R.string.query_city_screen_search_empty)
                }
                Text(text = text)
            }
        }

        else -> {
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
                    items = state.queryResult,
                    key = { item -> item.id }
                ) { item ->
                    QueryResultItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        cityResult = item,
                        onCityClick = onCityClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun QueryResultItem(
    modifier: Modifier = Modifier,
    cityResult: CityEntity,
    onCityClick: (CityEntity) -> Unit,
) {
    Card(
        modifier = modifier
            .clip(CardDefaults.shape)
            .clickable {
                onCityClick(cityResult)
            }
    ) {
        Column(
            modifier = Modifier.padding(Normal)
        ) {
            Text(
                text = cityResult.name,
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = cityResult.country,
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = cityResult.latLng.prettyPrint(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun BoxScope.ErrorContent(
    modifier: Modifier,
    state: QueryCityState,
    onRetryClicked: () -> Unit,
) {
    val msg = when {
        state.showGeneralError -> stringResource(R.string.query_city_screen_search_general_error)
        state.showInternetError -> stringResource(R.string.query_city_screen_search_no_internet_error)
        else -> ""
    }

    if (msg.isNotBlank()) {
        LocalSoftwareKeyboardController.current?.hide()
        Card(
            modifier = modifier
                .padding(Normal)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors()
                .copy(containerColor = MaterialTheme.colorScheme.errorContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = Small),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Normal)
                        .padding(top = Normal),
                    text = msg,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Normal)
                        .padding(bottom = Normal, top = HalfNormal),
                    onClick = onRetryClicked
                ) {
                    Text(
                        text = stringResource(R.string.query_city_screen_search_retry),
                        color = Color.White,
                    )
                }
            }
        }
    }
}
