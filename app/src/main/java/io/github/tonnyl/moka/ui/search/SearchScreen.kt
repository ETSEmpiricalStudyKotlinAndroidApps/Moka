package io.github.tonnyl.moka.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesScreen
import io.github.tonnyl.moka.ui.search.users.SearchedUsersScreen
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.widget.SearchBar

private enum class SearchType {

    Repositories,

    Users,

}

@ExperimentalComposeUiApi
@Composable
fun SearchScreen(navController: NavController) {
    val viewModel = viewModel<SearchViewModel>()
    val textState = remember { mutableStateOf(TextFieldValue()) }

    var usersFlow by remember { mutableStateOf(viewModel.usersFlow) }
    var repositoriesFlow by remember { mutableStateOf(viewModel.repositoriesFlow) }

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SearchScreenContent(
            topAppBarSize = topAppBarSize,
            navController = navController,
            users = usersFlow?.collectAsLazyPagingItems(),
            repositories = repositoriesFlow?.collectAsLazyPagingItems()
        )

        SearchBar(
            hintResId = R.string.search_input_hint,
            navController = navController,
            textState = textState,
            onImeActionPerformed = {
                viewModel.updateInput(textState.value.text)

                usersFlow = viewModel.usersFlow
                repositoriesFlow = viewModel.repositoriesFlow
            },
            elevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun SearchScreenContent(
    topAppBarSize: Int,
    navController: NavController,
    users: LazyPagingItems<SearchedUserOrOrgItem>?,
    repositories: LazyPagingItems<RepositoryItem>?
) {
    Column(
        modifier = Modifier
            .padding(
                top = LocalWindowInsets.current.systemBars.toPaddingValues(
                    top = false,
                    additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
                ).calculateTopPadding()
            )
    ) {
        var selectedTabIndex by remember { mutableStateOf(SearchType.values().first()) }
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
            SearchType.values().forEach { searchType ->
                Tab(
                    selected = searchType == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = searchType
                    },
                    text = {
                        Text(
                            text = stringResource(
                                when (searchType) {
                                    SearchType.Users -> {
                                        R.string.search_tab_users
                                    }
                                    SearchType.Repositories -> {
                                        R.string.search_tab_repositories
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
            SearchType.Users -> {
                users?.let {
                    SearchedUsersScreen(
                        navController = navController,
                        users = it
                    )
                }
            }
            SearchType.Repositories -> {
                repositories?.let {
                    SearchedRepositoriesScreen(
                        navController = navController,
                        repositories = it
                    )
                }
            }
        }
    }
}