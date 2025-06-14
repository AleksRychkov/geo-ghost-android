package dev.aleksrychkov.geoghost.feature.permission.ui.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.feature.permission.ui.DefaultPermissionScreen
import dev.aleksrychkov.geoghost.feature.permission.ui.R

@Composable
fun NotificationPermissionScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
) {
    val vm: NotificationPermissionViewModel =
        viewModel { NotificationPermissionViewModel(onFinish) }

    DefaultPermissionScreen(
        modifier = modifier,
        viewModel = vm,
        title = R.string.notification_screen_title,
        subtitle = R.string.notification_screen_subtitle,
        onFinish = onFinish,
    )
}
