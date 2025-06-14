package dev.aleksrychkov.geoghost.feature.map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aleksrychkov.geoghost.core.designsystem.composables.AnimatedBottomSheet
import dev.aleksrychkov.geoghost.core.designsystem.theme.Large
import dev.aleksrychkov.geoghost.core.designsystem.theme.Normal2X
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.bookmark.ui.form.BookmarkFormScreen
import dev.aleksrychkov.geoghost.feature.map.provider.api.GeoGhostMapActionsHandler
import dev.aleksrychkov.geoghost.feature.menu.ui.MenuScreen
import dev.aleksrychkov.geoghost.feature.permission.ui.location.LocationPermissionScreen
import dev.aleksrychkov.geoghost.feature.permission.ui.mock.RequestMockLocationScreen
import dev.aleksrychkov.geoghost.feature.permission.ui.notification.NotificationPermissionScreen

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
) {
    val vm = viewModel { MapScreenViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(vm) {
        lifecycleOwner.lifecycle.addObserver(vm)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(vm)
        }
    }

    MapScreenInner(
        modifier = modifier,
        state = state,
        scrollToCurrentPositionClicked = vm::scrollToCurrentPositionClicked,
        attachMapHandler = vm::attachMapHandler,
        detachMapHandler = vm::detachMapHandler,
        onMapActionTmpLocationSet = vm::onMapActionTmpLocationSet,
        startGhostLocation = vm::startGhostLocation,
        stopGhostLocation = vm::stopGhostLocation,
        onMapReady = vm::mapIsReady,
        showLocationMockingModal = vm::showLocationMockingModal,
        dismissLocationMockingModal = vm::dismissLocationMockingModal,
        showLocationPermissionModal = vm::showLocationPermissionModal,
        dismissLocationPermissionModal = vm::dismissLocationPermissionModal,
        showPermissionLocationModal = vm::showPermissionLocationModal,
        dismissPermissionLocationModal = vm::dismissPermissionLocationModal,
        onTmpMarkerClick = vm::onTmpMarkerClick,
        onGhostMarkerClick = vm::onGhostMarkerClick,
        showMenuModal = vm::showMenuModal,
        dismissMenuModal = vm::dismissMenuModal,
        dismissMenuModalWithLatLng = vm::dismissMenuModalWithLatLng,
        dismissBookmarkModal = vm::dismissBookmarkModal,
        dismissMenuModalWithBookmarkEntity = vm::dismissMenuModalWithBookmarkEntity,
    )
}

@Composable
private fun MapScreenInner(
    modifier: Modifier = Modifier,
    state: MapScreenState,
    scrollToCurrentPositionClicked: () -> Unit,
    attachMapHandler: (GeoGhostMapActionsHandler) -> Unit,
    detachMapHandler: () -> Unit,
    onMapActionTmpLocationSet: (LatLng) -> Unit,
    onMapReady: () -> Unit,
    stopGhostLocation: () -> Unit,
    startGhostLocation: () -> Unit,
    showLocationMockingModal: () -> Unit,
    dismissLocationMockingModal: () -> Unit,
    showLocationPermissionModal: () -> Unit,
    dismissLocationPermissionModal: () -> Unit,
    showPermissionLocationModal: () -> Unit,
    dismissPermissionLocationModal: () -> Unit,
    onTmpMarkerClick: () -> Unit,
    onGhostMarkerClick: () -> Unit,
    showMenuModal: () -> Unit,
    dismissMenuModal: () -> Unit,
    dismissMenuModalWithLatLng: (LatLng) -> Unit,
    dismissMenuModalWithBookmarkEntity: (BookmarkEntity) -> Unit,
    dismissBookmarkModal: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        content = { innerPadding ->
            Content(
                modifier = Modifier.fillMaxSize(),
                innerPadding = innerPadding,
                state = state,
                attachMapHandler = attachMapHandler,
                detachMapHandler = detachMapHandler,
                onMapActionTmpLocationSet = onMapActionTmpLocationSet,
                startGhostLocation = startGhostLocation,
                stopGhostLocation = stopGhostLocation,
                onMapReady = onMapReady,
                showLocationMockingModal = showLocationMockingModal,
                dismissLocationMockingModal = dismissLocationMockingModal,
                dismissLocationPermissionModal = dismissLocationPermissionModal,
                showPermissionLocationModal = showPermissionLocationModal,
                dismissPermissionLocationModal = dismissPermissionLocationModal,
                onTmpMarkerClick = onTmpMarkerClick,
                onGhostMarkerClick = onGhostMarkerClick,
                dismissMenuModal = dismissMenuModal,
                dismissMenuModalWithLatLng = dismissMenuModalWithLatLng,
                dismissBookmarkModal = dismissBookmarkModal,
                dismissMenuModalWithBookmarkEntity = dismissMenuModalWithBookmarkEntity,
            )
        },
        floatingActionButton = {
            MapFloatingActionButton(
                isLocationEnabled = state.isLocationPermissionEnabled,
                showLocationPermissionModal = showLocationPermissionModal,
                scrollToCurrentPositionClicked = scrollToCurrentPositionClicked,
                showMenuModal = showMenuModal,
            )
        }
    )
}

@Composable
private fun MapFloatingActionButton(
    isLocationEnabled: Boolean,
    showLocationPermissionModal: () -> Unit,
    scrollToCurrentPositionClicked: () -> Unit,
    showMenuModal: () -> Unit,
) {
    Column {
        FloatingActionButton(
            onClick = {
                if (!isLocationEnabled) {
                    showLocationPermissionModal()
                } else {
                    scrollToCurrentPositionClicked()
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val contentLocationDescription =
                if (isLocationEnabled) {
                    stringResource(R.string.map_screen_scroll_to_current_location)
                } else {
                    stringResource(R.string.map_screen_enable_location_permission)
                }
            val id = if (isLocationEnabled) {
                R.drawable.ic_near_me_24px
            } else {
                R.drawable.ic_near_me_disabled_24px
            }
            Icon(
                painter = painterResource(id),
                contentDescription = contentLocationDescription
            )
        }

        Spacer(modifier = Modifier.height(Normal2X))

        FloatingActionButton(
            onClick = showMenuModal
        ) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = stringResource(R.string.map_screen_open_menu)
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    innerPadding: PaddingValues,
    state: MapScreenState,
    attachMapHandler: (GeoGhostMapActionsHandler) -> Unit,
    detachMapHandler: () -> Unit,
    onMapActionTmpLocationSet: (LatLng) -> Unit,
    onMapReady: () -> Unit,
    stopGhostLocation: () -> Unit,
    startGhostLocation: () -> Unit,
    showLocationMockingModal: () -> Unit,
    dismissLocationMockingModal: () -> Unit,
    dismissLocationPermissionModal: () -> Unit,
    showPermissionLocationModal: () -> Unit,
    dismissPermissionLocationModal: () -> Unit,
    onTmpMarkerClick: () -> Unit,
    onGhostMarkerClick: () -> Unit,
    dismissMenuModal: () -> Unit,
    dismissMenuModalWithLatLng: (LatLng) -> Unit,
    dismissMenuModalWithBookmarkEntity: (BookmarkEntity) -> Unit,
    dismissBookmarkModal: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
    ) {
        val density = LocalDensity.current
        MapWrapper(
            modifier = Modifier.fillMaxSize(),
            attachMapHandler = attachMapHandler,
            detachMapHandler = detachMapHandler,
            paddingTop = with(density) { innerPadding.calculateTopPadding().roundToPx() },
            paddingEnd = with(density) { Large.roundToPx() },
            onMapActionTmpLocationSet = onMapActionTmpLocationSet,
            onMapReady = onMapReady,
            onTmpMarkerClick = onTmpMarkerClick,
            onGhostMarkerClick = onGhostMarkerClick,
        )

        MapContentLocationHandlers(
            modifier = Modifier
                .wrapContentSize()
                .navigationBarsPadding()
                .padding(start = Large, bottom = Large),
            state = state,
            startGhostLocation = startGhostLocation,
            stopGhostLocation = stopGhostLocation,
            showPermissionLocationModal = showPermissionLocationModal,
            showLocationMockingModal = showLocationMockingModal,
        )

        EnableLocationMockingModal(
            isEnabled = state.isLocationMockModalEnabled,
            dismiss = dismissLocationMockingModal,
        )

        EnableLocationPermissionModal(
            isEnabled = state.isLocationPermissionModalEnabled,
            dismiss = dismissLocationPermissionModal,
        )

        EnableNotificationPermissionModal(
            isEnabled = state.isNotificationPermissionModalEnabled,
            dismiss = dismissPermissionLocationModal,
        )

        EnableMenuModal(
            isEnabled = state.isMenuModalEnabled,
            dismiss = dismissMenuModal,
            dismissWithLatLng = dismissMenuModalWithLatLng,
            dismissWithBookmarkEntity = dismissMenuModalWithBookmarkEntity,
        )

        EnableBookmarkModal(
            isEnabled = state.isBookmarkModalEnabled && state.ghostLocation != null,
            latLng = state.ghostLocation,
            dismiss = dismissBookmarkModal,
        )
    }
}

@Composable
private fun MapContentLocationHandlers(
    modifier: Modifier = Modifier,
    state: MapScreenState,
    stopGhostLocation: () -> Unit,
    startGhostLocation: () -> Unit,
    showPermissionLocationModal: () -> Unit,
    showLocationMockingModal: () -> Unit,
) {
    if (state.ghostLocation == null && state.tmpLatLng == null) return
    Box(
        modifier = modifier,
    ) {
        FloatingActionButton(
            onClick = {
                if (!state.isLocationMockEnabled) {
                    showLocationMockingModal()
                    return@FloatingActionButton
                }

                if (state.shouldAskNotificationPermission) {
                    showPermissionLocationModal()
                    return@FloatingActionButton
                }

                if (state.ghostLocation != null) {
                    stopGhostLocation()
                } else {
                    startGhostLocation()
                }
            },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {

            if (state.ghostLocation != null) {
                Icon(
                    painter = painterResource(R.drawable.ic_stop_24px),
                    contentDescription = stringResource(R.string.map_screen_stop_ghost_location)
                )
            } else {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.map_screen_stop_ghost_location)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnableLocationMockingModal(
    isEnabled: Boolean,
    dismiss: () -> Unit,
) {
    AnimatedBottomSheet(
        isVisible = isEnabled,
        onDismissRequest = dismiss
    ) {

        RequestMockLocationScreen(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            dismiss = dismiss,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnableLocationPermissionModal(
    isEnabled: Boolean,
    dismiss: () -> Unit,
) {
    AnimatedBottomSheet(
        isVisible = isEnabled,
        onDismissRequest = dismiss
    ) {
        LocationPermissionScreen(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            onFinish = dismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnableNotificationPermissionModal(
    isEnabled: Boolean,
    dismiss: () -> Unit,
) {
    AnimatedBottomSheet(
        isVisible = isEnabled,
        onDismissRequest = dismiss
    ) {
        NotificationPermissionScreen(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            onFinish = dismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnableMenuModal(
    isEnabled: Boolean,
    dismissWithLatLng: (LatLng) -> Unit,
    dismiss: () -> Unit,
    dismissWithBookmarkEntity: (BookmarkEntity) -> Unit,
) {
    AnimatedBottomSheet(
        isVisible = isEnabled,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = dismiss
    ) {
        MenuScreen(
            modifier = Modifier.fillMaxWidth(),
            dismissWithLatLng = dismissWithLatLng,
            dismissWithBookmarkEntity = dismissWithBookmarkEntity,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnableBookmarkModal(
    isEnabled: Boolean,
    latLng: LatLng?,
    dismiss: () -> Unit,
) {
    if (latLng == null) return
    AnimatedBottomSheet(
        isVisible = isEnabled,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = dismiss
    ) {
        BookmarkFormScreen(
            modifier = Modifier.fillMaxWidth(),
            latLng = latLng,
            dismiss = dismiss,
        )
    }
}
