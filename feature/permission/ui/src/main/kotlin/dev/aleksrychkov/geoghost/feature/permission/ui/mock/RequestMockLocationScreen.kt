package dev.aleksrychkov.geoghost.feature.permission.ui.mock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.core.designsystem.composables.requestAccess
import dev.aleksrychkov.geoghost.feature.permission.ui.R

@Composable
fun RequestMockLocationScreen(
    modifier: Modifier,
    dismiss: () -> Unit,
) {
    val vm: RequestMockLocationViewModel = viewModel {
        RequestMockLocationViewModel(dismiss = dismiss)
    }

    val context = LocalContext.current

    requestAccess(
        modifier = modifier,
        title = stringResource(R.string.location_mock_screen_title),
        subtitle = stringResource(R.string.location_mock_screen_subtitle),
        btnDenyText = stringResource(R.string.location_mock_screen_cancel),
        btnEnableText = stringResource(R.string.location_mock_screen_open_developer_options),
        onEnable = { vm.navigateToDeveloperOptions(context) },
        onDeny = dismiss
    )
}
