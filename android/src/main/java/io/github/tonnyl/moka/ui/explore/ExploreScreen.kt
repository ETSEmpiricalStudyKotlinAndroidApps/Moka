package io.github.tonnyl.moka.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.extension.displayStringResId
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.ListSubheader
import io.github.tonnyl.moka.widget.MainSearchBar
import io.tonnyl.moka.common.data.FiltersType
import io.tonnyl.moka.common.db.data.TrendingDeveloper
import io.tonnyl.moka.common.db.data.TrendingRepository
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreTimeSpan
import io.tonnyl.moka.common.store.data.urlParamValue
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.TrendingDeveloperProvider
import io.tonnyl.moka.common.util.TrendingRepositoryProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalSerializationApi
@Composable
@ExperimentalMaterialApi
fun ExploreScreen(openDrawer: (() -> Unit)?) {
    val currentAccount = LocalAccountInstance.current ?: return

    val exploreViewModel = viewModel(
        key = LocalAccountInstance.current.toString(),
        initializer = {
            ExploreViewModel(
                extra = ExploreViewModelExtra(
                    accountInstance = currentAccount
                )
            )
        }
    )

    val exploreOptions by exploreViewModel.options.observeAsState(initial = ExploreOptionsSerializer.defaultValue)

    val trendingDevelopers by exploreViewModel.developersLocalData.observeAsState(emptyList())
    val trendingRepositories by exploreViewModel.repositoriesLocalData.observeAsState(emptyList())

    val refreshStatus by exploreViewModel.refreshDataStatus.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(
                    isRefreshing = refreshStatus?.status == Status.LOADING
                ),
                onRefresh = {
                    exploreViewModel.refreshTrendingData()
                },
                indicatorPadding = contentPaddings,
                indicator = { state, refreshTriggerDistance ->
                    DefaultSwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance
                    )
                }
            ) {
                ExploreScreenContent(
                    exploreOptions = exploreOptions,
                    contentPadding = contentPaddings,
                    trendingRepositories = trendingRepositories,
                    trendingDevelopers = trendingDevelopers,
                    enablePlaceholder = refreshStatus?.status == Status.LOADING,
                    viewModel = exploreViewModel
                )
            }

            MainSearchBar(
                openDrawer = openDrawer,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { topAppBarSize = it.height }
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false)
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
private fun ExploreScreenContent(
    contentPadding: PaddingValues,
    exploreOptions: ExploreOptions,
    viewModel: ExploreViewModel,
    trendingRepositories: List<TrendingRepository>,
    trendingDevelopers: List<TrendingDeveloper>,
    enablePlaceholder: Boolean
) {
    val developersHorizontalScrollState = rememberLazyListState()
    val repositoryPlaceholder = remember {
        TrendingRepositoryProvider().values.first()
    }
    val developerPlaceholder = remember {
        TrendingDeveloperProvider().values.first()
    }

    LazyColumn(contentPadding = contentPadding) {
        item {
            ListSubheader(
                text = stringResource(id = R.string.explore_title),
                enablePlaceholder = enablePlaceholder
            )
        }

        item {
            ExploreFiltersHeader(
                exploreOptions = exploreOptions,
                enablePlaceholder = enablePlaceholder,
                viewModel = viewModel
            )
        }

        item {
            LazyRow(
                state = developersHorizontalScrollState,
                contentPadding = PaddingValues(horizontal = ContentPaddingLargeSize)
            ) {
                items(
                    count = if (enablePlaceholder) {
                        defaultPagingConfig.initialLoadSize
                    } else {
                        trendingDevelopers.size
                    }
                ) { index ->
                    TrendingDeveloperItem(
                        index = index,
                        developer = if (enablePlaceholder) {
                            developerPlaceholder
                        } else {
                            trendingDevelopers[index]
                        },
                        enablePlaceholder = enablePlaceholder
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }

        items(
            count = if (enablePlaceholder) {
                defaultPagingConfig.initialLoadSize
            } else {
                trendingRepositories.size
            }
        ) { index ->
            TrendingRepositoryItem(
                timeSpanText = exploreOptions.timeSpan.urlParamValue,
                repository = if (enablePlaceholder) {
                    repositoryPlaceholder
                } else {
                    trendingRepositories[index]
                },
                enablePlaceholder = enablePlaceholder
            )
        }
    }
}

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
private fun ExploreFiltersHeader(
    viewModel: ExploreViewModel,
    exploreOptions: ExploreOptions,
    enablePlaceholder: Boolean
) {
    val timeSpanMenuState = remember { mutableStateOf(value = false) }
    val navController = LocalNavController.current

    LazyRow(
        contentPadding = PaddingValues(
            start = ContentPaddingLargeSize,
            end = ContentPaddingLargeSize,
            bottom = ContentPaddingLargeSize
        ),
        modifier = Modifier.fillMaxWidth()
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Horizontal
            )
    ) {
        item {
            Box {
                Chip(
                    enabled = !enablePlaceholder,
                    onClick = {
                        timeSpanMenuState.value = true
                    }
                ) {
                    Text(
                        text = stringResource(id = exploreOptions.timeSpan.displayStringResId),
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            color = MaterialTheme.colors.onSurface.copy(alpha = .1f),
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    DownArrow(enablePlaceholder = enablePlaceholder)
                }
                TimespanDropDownMenus(
                    showMenuState = timeSpanMenuState,
                    viewModel = viewModel
                )
            }
        }
        item {
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
        }
        item {
            Chip(
                enabled = !enablePlaceholder,
                onClick = {
                    Screen.ExploreFilters.navigate(
                        navController = navController,
                        type = FiltersType.ProgrammingLanguages
                    )
                }
            ) {
                Text(
                    text = exploreOptions.exploreLanguage.name,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        color = MaterialTheme.colors.onSurface.copy(alpha = .1f),
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                DownArrow(enablePlaceholder = enablePlaceholder)
            }
        }
        item {
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
        }
        item {
            Chip(
                enabled = !enablePlaceholder,
                onClick = {
                    Screen.ExploreFilters.navigate(
                        navController = navController,
                        type = FiltersType.SpokenLanguages
                    )
                }
            ) {
                Text(
                    text = exploreOptions.exploreSpokenLanguage.name,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        color = MaterialTheme.colors.onSurface.copy(alpha = .1f),
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                DownArrow(enablePlaceholder = enablePlaceholder)
            }
        }
    }
}

@Composable
private fun RowScope.DownArrow(enablePlaceholder: Boolean) {
    if (enablePlaceholder) {
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        Box(
            modifier = Modifier
                .size(size = RepositoryCardIconSize)
                .align(alignment = Alignment.CenterVertically)
                .placeholder(
                    visible = true,
                    color = MaterialTheme.colors.onSurface.copy(alpha = .1f),
                    highlight = PlaceholderHighlight.fade()
                )
        )
    } else {
        Image(
            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = null
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun TimespanDropDownMenus(
    showMenuState: MutableState<Boolean>,
    viewModel: ExploreViewModel
) {
    DropdownMenu(
        expanded = showMenuState.value,
        offset = DpOffset(
            x = 0.dp,
            y = -ContentPaddingSmallSize
        ),
        onDismissRequest = {
            showMenuState.value = false
        }
    ) {
        DropdownMenuItem(
            onClick = {
                viewModel.updateExploreOptions(
                    timeSpan = ExploreTimeSpan.DAILY
                )

                showMenuState.value = false
            }
        ) {
            Text(text = stringResource(id = R.string.explore_trending_filter_time_span_daily))
        }
        DropdownMenuItem(
            onClick = {
                viewModel.updateExploreOptions(
                    timeSpan = ExploreTimeSpan.WEEKLY
                )

                showMenuState.value = false
            }
        ) {
            Text(text = stringResource(id = R.string.explore_trending_filter_time_span_weekly))
        }
        DropdownMenuItem(
            onClick = {
                viewModel.updateExploreOptions(
                    timeSpan = ExploreTimeSpan.MONTHLY
                )

                showMenuState.value = false
            }) {
            Text(text = stringResource(id = R.string.explore_trending_filter_time_span_monthly))
        }
    }
}