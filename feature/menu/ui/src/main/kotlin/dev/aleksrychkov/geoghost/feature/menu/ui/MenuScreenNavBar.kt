package dev.aleksrychkov.geoghost.feature.menu.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource

@Composable
internal fun MenuScreenPagerNavBar(
    searchCitySelected: Boolean,
    bookmarksSelected: Boolean,
    openSearchCity: () -> Unit,
    openBookmarks: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = searchCitySelected,
            onClick = openSearchCity,
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.menu_screen_nav_bar_item_search_title)
                )
            }
        )
        NavigationBarItem(
            selected = bookmarksSelected,
            onClick = openBookmarks,
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = dev.aleksrychkov.geoghost.core.designsystem.R.drawable.ic_bookmark_24),
                    contentDescription = stringResource(R.string.menu_screen_nav_bar_item_bookmarks_title)
                )
            }
        )
    }
}
