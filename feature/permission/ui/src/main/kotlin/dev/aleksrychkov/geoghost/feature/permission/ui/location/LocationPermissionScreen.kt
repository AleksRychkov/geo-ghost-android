package dev.aleksrychkov.geoghost.feature.permission.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.feature.permission.ui.DefaultPermissionScreen
import dev.aleksrychkov.geoghost.feature.permission.ui.R

@Composable
fun LocationPermissionScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
) {
    val vm: LocationPermissionViewModel =
        viewModel { LocationPermissionViewModel(onFinish) }

    DefaultPermissionScreen(
        modifier = modifier,
        viewModel = vm,
        title = R.string.location_screen_title,
        subtitle = R.string.location_screen_subtitle,
        onFinish = onFinish,
    )
}
