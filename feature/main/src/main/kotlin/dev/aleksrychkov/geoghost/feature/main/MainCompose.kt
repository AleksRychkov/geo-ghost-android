package dev.aleksrychkov.geoghost.feature.main

import androidx.compose.runtime.Composable
import dev.aleksrychkov.geoghost.core.designsystem.theme.GeoGhostTheme
import dev.aleksrychkov.geoghost.feature.map.ui.MapScreen

@Composable
internal fun Main() {
    GeoGhostTheme {
        MapScreen()
    }
}
