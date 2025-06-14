package dev.aleksrychkov.geoghost.feature.bookmark.ui.form

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.bookmark.ui.R
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal
import dev.aleksrychkov.geoghost.core.model.LatLng

@Composable
fun BookmarkFormScreen(
    modifier: Modifier = Modifier,
    latLng: LatLng,
    dismiss: () -> Unit,
) {
    BookmarkFormScreenInner(
        modifier = modifier,
        latLng = latLng,
        dismiss = dismiss,
    )
}

@Composable
private fun BookmarkFormScreenInner(
    modifier: Modifier,
    latLng: LatLng,
    dismiss: () -> Unit,
) {
    val vm: BookmarkFormViewModel =
        viewModel { BookmarkFormViewModel(dismiss = dismiss) }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(latLng) {
        vm.setLatLng(latLng)
    }
    DisposableEffect(vm) {
        onDispose {
            vm.onNameInputChanged("")
        }
    }

    Content(
        modifier = modifier,
        state = state,
        onNameInputChanged = vm::onNameInputChanged,
        onAddClicked = vm::addBookmark,
        onRemoveClicked = vm::deleteBookmark,
    )
}

@Composable
private fun Content(
    modifier: Modifier,
    state: BookmarkFormState,
    onNameInputChanged: (String) -> Unit,
    onAddClicked: (Context) -> Unit,
    onRemoveClicked: (Context) -> Unit,
) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(Normal)
    ) {

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Normal)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            value = state.name,
            onValueChange = {
                onNameInputChanged(it)
            },

            singleLine = true,
            enabled = state.entity == null,
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Normal),
            text = state.location,
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Normal),
            onClick = {
                if (state.entity == null) {
                    onAddClicked(context)
                } else {
                    onRemoveClicked(context)
                }
                localFocusManager.clearFocus(force = true)
            },
            colors = if (state.entity == null) {
                ButtonDefaults.buttonColors()
            } else {
                ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.error)
            }
        ) {
            val text = if (state.entity == null) {
                stringResource(R.string.bookmark_screen_add_bookmark)
            } else {
                stringResource(R.string.bookmark_screen_remove_bookmark)
            }
            Text(text = text)
        }
    }
}