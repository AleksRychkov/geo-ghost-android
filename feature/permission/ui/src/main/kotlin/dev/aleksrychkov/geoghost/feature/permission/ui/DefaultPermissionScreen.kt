package dev.aleksrychkov.geoghost.feature.permission.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.aleksrychkov.geoghost.core.designsystem.composables.requestAccess

@Composable
internal fun DefaultPermissionScreen(
    modifier: Modifier = Modifier,
    viewModel: AbstractPermissionViewModel,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onFinish: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(viewModel) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    Content(
        modifier = modifier,
        state = state,
        permissionDenied = onFinish,
        title = title,
        subtitle = subtitle,
        enableClicked = viewModel::requestPermission,
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: PermissionState,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    permissionDenied: () -> Unit,
    enableClicked: (Context) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        } else {
            HandlePermissionContent(
                modifier = Modifier.fillMaxSize(),
                title = title,
                subtitle = subtitle,
                permissionDenied = permissionDenied,
                enableClicked = enableClicked,
            )
        }
    }
}

@Composable
private fun HandlePermissionContent(
    modifier: Modifier = Modifier,
    permissionDenied: () -> Unit,
    enableClicked: (Context) -> Unit,
    @StringRes title: Int,
    @StringRes subtitle: Int,
) {
    val context = LocalContext.current

    requestAccess(
        modifier = modifier,
        title = stringResource(title),
        subtitle = stringResource(subtitle),
        btnDenyText = stringResource(R.string.permission_screen_deny),
        btnEnableText = stringResource(R.string.permission_screen_enable),
        onEnable = { enableClicked(context) },
        onDeny = permissionDenied
    )
}