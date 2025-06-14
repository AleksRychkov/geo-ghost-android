package dev.aleksrychkov.geoghost.feature.menu.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.aleksrychkov.geoghost.core.model.BookmarkEntity
import dev.aleksrychkov.geoghost.core.model.LatLng
import dev.aleksrychkov.geoghost.feature.bookmark.ui.list.BookmarkSelectScreen
import dev.aleksrychkov.geoghost.feature.query.city.ui.QueryCityScreen
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    modifier: Modifier,
    dismissWithLatLng: (LatLng) -> Unit,
    dismissWithBookmarkEntity: (BookmarkEntity) -> Unit,
) {
    MenuScreenPager(
        modifier = modifier,
        dismissWithLatLng = dismissWithLatLng,
        dismissWithBookmarkEntity = dismissWithBookmarkEntity,
    )
}

@Composable
private fun MenuScreenPager(
    modifier: Modifier,
    dismissWithLatLng: (LatLng) -> Unit,
    dismissWithBookmarkEntity: (BookmarkEntity) -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { 2 },
    )
    val coroutineScope = rememberCoroutineScope()
    BoxWithConstraints {
        Scaffold(
            modifier = modifier.height(maxHeight * 0.75f),
            bottomBar = {
                MenuScreenPagerNavBar(
                    searchCitySelected = pagerState.currentPage == 0,
                    bookmarksSelected = pagerState.currentPage == 1,
                    openSearchCity = {
                        if (pagerState.currentPage != 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 0,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                )
                            }
                        }
                    },
                    openBookmarks = {
                        if (pagerState.currentPage != 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 1,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                )
                            }
                        }
                    },
                )
            }
        ) { innerPadding ->
            MenuScreenPagerContent(
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                pagerState = pagerState,
                dismissWithLatLng = dismissWithLatLng,
                dismissWithBookmarkEntity = dismissWithBookmarkEntity,
            )
        }
    }
}

@Composable
private fun MenuScreenPagerContent(
    modifier: Modifier,
    pagerState: PagerState,
    dismissWithLatLng: (LatLng) -> Unit,
    dismissWithBookmarkEntity: (BookmarkEntity) -> Unit,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        beyondViewportPageCount = 1,
    ) { page ->
        if (page == 0) {
            QueryCityScreen(
                modifier = Modifier.fillMaxSize(),
                onCityLatLngClicked = dismissWithLatLng
            )
        } else {
            BookmarkSelectScreen(
                modifier = Modifier.fillMaxSize(),
                onBookmarkClicked = dismissWithBookmarkEntity,
            )
        }
    }
}
