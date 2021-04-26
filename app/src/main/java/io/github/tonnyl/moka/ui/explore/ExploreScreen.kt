package io.github.tonnyl.moka.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.widget.MainSearchBar
import kotlinx.coroutines.launch

private enum class ExploreCategory {

    Repositories,

    Developers,

}

@Composable
@ExperimentalMaterialApi
fun ExploreScreen(
    openDrawer: () -> Unit,
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val exploreViewModel = viewModel<ExploreViewModel>(
        key = LocalAccountInstance.current.toString(),
        factory = ViewModelFactory(accountInstance = currentAccount)
    )

    val trendingDevelopers by exploreViewModel.developersLocalData.observeAsState(emptyList())
    val trendingRepositories by exploreViewModel.repositoriesLocalData.observeAsState(emptyList())

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ExploreFiltersScreen(
        navController = navController,
        viewModel = mainViewModel,
        sheetState = bottomSheetState
    ) {
        var topAppBarSize by remember { mutableStateOf(0) }

        Box {
            MainSearchBar(
                openDrawer = openDrawer,
                mainViewModel = mainViewModel,
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { topAppBarSize = it.height }
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false)
            )

            ExploreScreenContent(
                topAppBarSize = topAppBarSize,
                trendingRepositories = trendingRepositories,
                trendingDevelopers = trendingDevelopers
            )

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
        }
    }
}

@Composable
private fun ExploreScreenContent(
    topAppBarSize: Int,
    trendingRepositories: List<TrendingRepository>,
    trendingDevelopers: List<TrendingDeveloper>
) {
    Column(
        modifier = Modifier.padding(
            top = LocalWindowInsets.current.systemBars.toPaddingValues(
                top = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            ).calculateTopPadding()
        )
    ) {
        var selectedTabIndex by remember { mutableStateOf(ExploreCategory.Repositories) }
        TabRow(
            selectedTabIndex = selectedTabIndex.ordinal,
            indicator = @Composable { tabPositions: List<TabPosition> ->
                Spacer(
                    Modifier
                        .tabIndicatorOffset(currentTabPosition = tabPositions[selectedTabIndex.ordinal])
                        .padding(horizontal = 24.dp)
                        .height(height = ContentPaddingSmallSize)
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(
                                topStartPercent = 100,
                                topEndPercent = 100
                            )
                        )
                )
            },
            backgroundColor = MaterialTheme.colors.background
        ) {
            ExploreCategory.values().forEach { exploreCategory ->
                Tab(
                    selected = exploreCategory == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = exploreCategory
                    },
                    text = {
                        Text(
                            text = stringResource(
                                when (exploreCategory) {
                                    ExploreCategory.Repositories -> {
                                        R.string.explore_trending_repositories
                                    }
                                    ExploreCategory.Developers -> {
                                        R.string.explore_trending_developers
                                    }
                                }
                            ),
                            style = MaterialTheme.typography.body2
                        )
                    },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }

        when (selectedTabIndex) {
            ExploreCategory.Repositories -> {
                TrendingRepositoriesScreen(repositories = trendingRepositories)
            }
            ExploreCategory.Developers -> {
                TrendingDevelopersScreen(developers = trendingDevelopers)
            }
        }
    }
}

@Composable
@Preview(name = "ExploreScreenContentPreview", showBackground = true)
private fun ExploreScreenContentPreview() {
    ExploreScreenContent(
        topAppBarSize = 0,
        trendingRepositories = emptyList(),
        trendingDevelopers = emptyList()
    )
}