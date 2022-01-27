package io.github.tonnyl.moka.ui.explore

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.extension.displayStringResId
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.ListSubheader
import io.github.tonnyl.moka.widget.MainSearchBar
import io.tonnyl.moka.common.db.data.TrendingDeveloper
import io.tonnyl.moka.common.db.data.TrendingRepository
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.urlParamValue
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.TrendingDeveloperProvider
import io.tonnyl.moka.common.util.TrendingRepositoryProvider
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalSerializationApi
@Composable
@ExperimentalMaterialApi
fun ExploreScreen(openDrawer: (() -> Unit)?) {
    val currentAccount = LocalAccountInstance.current ?: return

    val exploreViewModel = viewModel<ExploreViewModel>(
        key = LocalAccountInstance.current.toString(),
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[ExploreViewModel.EXPLORE_VIEW_MODEL_EXTRA_KEY] = ExploreViewModelExtra(
                accountInstance = currentAccount
            )
        }
    )

    val exploreOptions by exploreViewModel.queryData.observeAsState(initial = ExploreOptionsSerializer.defaultValue)

    val trendingDevelopers by exploreViewModel.developersLocalData.observeAsState(emptyList())
    val trendingRepositories by exploreViewModel.repositoriesLocalData.observeAsState(emptyList())

    val refreshStatus by exploreViewModel.refreshDataStatus.observeAsState()

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ExploreFiltersScreen(sheetState = bottomSheetState) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
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
                indicatorPadding = contentPadding,
                indicator = { state, refreshTriggerDistance ->
                    DefaultSwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance
                    )
                }
            ) {
                ExploreScreenContent(
                    exploreOptions = exploreOptions,
                    contentTopPadding = contentPadding.calculateTopPadding(),
                    trendingRepositories = trendingRepositories,
                    trendingDevelopers = trendingDevelopers,
                    enablePlaceholder = refreshStatus?.status == Status.LOADING
                            && refreshStatus?.data == true
                )
            }

            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                },
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(all = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.notification_filters),
                    painter = painterResource(id = R.drawable.ic_filter_24)
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
private fun ExploreScreenContent(
    contentTopPadding: Dp,
    exploreOptions: ExploreOptions,
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ListSubheader(
                text = stringResource(
                    id = R.string.explore_title,
                    exploreOptions.exploreLanguage.name,
                    stringResource(id = exploreOptions.timeSpan.displayStringResId)
                ),
                enablePlaceholder = enablePlaceholder
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
@Preview(
    name = "ExploreScreenContentPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
private fun ExploreScreenContentPreview() {
    ExploreScreenContent(
        contentTopPadding = 0.dp,
        exploreOptions = ExploreOptionsSerializer.defaultValue,
        trendingRepositories = emptyList(),
        trendingDevelopers = emptyList(),
        enablePlaceholder = false
    )
}