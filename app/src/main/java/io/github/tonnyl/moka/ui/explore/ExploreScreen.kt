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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.serializers.store.ExploreOptionsSerializer
import io.github.tonnyl.moka.serializers.store.data.ExploreOptions
import io.github.tonnyl.moka.serializers.store.data.displayStringResId
import io.github.tonnyl.moka.serializers.store.data.urlParamValue
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.widget.ListSubheader
import io.github.tonnyl.moka.widget.MainSearchBar
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalSerializationApi
@Composable
@ExperimentalMaterialApi
fun ExploreScreen(openDrawer: () -> Unit) {
    val currentAccount = LocalAccountInstance.current ?: return

    val exploreViewModel = viewModel<ExploreViewModel>(
        key = LocalAccountInstance.current.toString(),
        factory = ViewModelFactory(accountInstance = currentAccount)
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

        val contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
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
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance,
                        scale = true,
                        contentColor = MaterialTheme.colors.secondary
                    )
                }
            ) {
                ExploreScreenContent(
                    exploreOptions = exploreOptions,
                    contentTopPadding = contentPadding.calculateTopPadding(),
                    trendingRepositories = trendingRepositories,
                    trendingDevelopers = trendingDevelopers
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

@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
private fun ExploreScreenContent(
    contentTopPadding: Dp,
    exploreOptions: ExploreOptions,
    trendingRepositories: List<TrendingRepository>,
    trendingDevelopers: List<TrendingDeveloper>
) {
    val developersHorizontalScrollState = rememberLazyListState()

    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ListSubheader(
                text = stringResource(
                    id = R.string.explore_title,
                    exploreOptions.exploreLanguage.name,
                    stringResource(id = exploreOptions.timeSpan.displayStringResId)
                )
            )
        }

        item {
            LazyRow(
                state = developersHorizontalScrollState,
                contentPadding = PaddingValues(horizontal = ContentPaddingLargeSize)
            ) {
                items(count = trendingDevelopers.size) { index ->
                    TrendingDeveloperItem(
                        index = index,
                        developer = trendingDevelopers[index]
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }

        items(count = trendingRepositories.size) { index ->
            TrendingRepositoryItem(
                timeSpanText = exploreOptions.timeSpan.urlParamValue,
                repository = trendingRepositories[index]
            )
        }
    }
}

@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
@Preview(name = "ExploreScreenContentPreview", showBackground = true)
private fun ExploreScreenContentPreview() {
    ExploreScreenContent(
        contentTopPadding = 0.dp,
        exploreOptions = ExploreOptionsSerializer.defaultValue,
        trendingRepositories = emptyList(),
        trendingDevelopers = emptyList()
    )
}