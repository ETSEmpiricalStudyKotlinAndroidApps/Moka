package io.github.tonnyl.moka.ui.search

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesScreen
import io.github.tonnyl.moka.ui.search.users.SearchedUsersScreen
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.widget.SearchBar
import io.github.tonnyl.moka.widget.ShareAndOpenInBrowserMenu
import io.tonnyl.moka.common.data.SearchedUserOrOrgItem
import io.tonnyl.moka.common.store.data.Query
import io.tonnyl.moka.common.store.data.SearchHistory
import io.tonnyl.moka.common.util.HistoryQueriesProvider
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

private enum class SearchType {

    Repositories,

    Users,

}

@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(initialSearchKeyword: String) {
    val currentAccount = LocalAccountInstance.current ?: return

    val app = LocalContext.current.applicationContext as Application
    val viewModel = viewModel(
        initializer = {
            SearchViewModel(
                extra = SearchScreenViewModelExtra(
                    accountInstance = currentAccount,
                    initialSearchKeyword = initialSearchKeyword
                ),
                app = app
            )
        }
    )
    val input = viewModel.userInput.value ?: ""
    val textState = remember {
        mutableStateOf(
            TextFieldValue(
                text = input,
                selection = TextRange(input.length)
            )
        )
    }

    var usersFlow by remember { mutableStateOf(viewModel.usersFlow) }
    var repositoriesFlow by remember { mutableStateOf(viewModel.repositoriesFlow) }

    val queryHistory by viewModel.queryHistoryStore
        .data
        .collectAsState(initial = SearchHistory())

    var displaySearchHistory by remember { mutableStateOf(true) }

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SearchScreenContent(
            topAppBarSize = topAppBarSize,
            users = usersFlow?.collectAsLazyPagingItems(),
            repositories = repositoriesFlow?.collectAsLazyPagingItems(),
            displaySearchHistory = displaySearchHistory,
            queries = queryHistory.queries,
            removeQuery = viewModel::removeQuery,
            onQueryClicked = {
                viewModel.updateInput(newInput = it.keyword)
            }
        )

        SearchBar(
            hintResId = R.string.search_input_hint,
            textState = textState,
            onImeActionPerformed = {
                viewModel.updateInput(textState.value.text)

                usersFlow = viewModel.usersFlow
                repositoriesFlow = viewModel.repositoriesFlow
            },
            onFocusChanged = { hasFocus ->
                displaySearchHistory = hasFocus
            },
            elevation = 0.dp,
            actions = {
                val inputText = textState.value.text
                if (inputText.isNotEmpty()) {
                    ShareAndOpenInBrowserMenu(
                        showMenuState = remember { mutableStateOf(false) },
                        text = "https://github.com/search?q=${inputText}"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
private fun SearchScreenContent(
    topAppBarSize: Int,
    users: LazyPagingItems<SearchedUserOrOrgItem>?,
    repositories: LazyPagingItems<RepositoryListItemFragment>?,
    displaySearchHistory: Boolean,
    queries: List<Query>,
    removeQuery: (Query) -> Unit,
    onQueryClicked: (Query) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0)

    Column(
        modifier = Modifier
            .padding(
                top = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = false,
                    additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
                ).calculateTopPadding()
            )
    ) {
        if (displaySearchHistory
            && queries.isNotEmpty()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = queries.size) { index ->
                    ItemSearchHistory(
                        query = queries[index],
                        removeQuery = removeQuery,
                        clickAction = onQueryClicked
                    )
                }
            }
        } else {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .pagerTabIndicatorOffset(
                                pagerState = pagerState,
                                tabPositions = tabPositions
                            )
                            .padding(horizontal = 24.dp)
                            .height(height = ContentPaddingSmallSize)
                            .clip(
                                shape = RoundedCornerShape(
                                    topStartPercent = 100,
                                    topEndPercent = 100
                                )
                            )
                    )
                }
            ) {
                val scope = rememberCoroutineScope()
                SearchType.values().forEachIndexed { index, type ->
                    Tab(
                        text = {
                            Text(
                                text = stringResource(
                                    when (type) {
                                        SearchType.Users -> {
                                            R.string.search_tab_users
                                        }
                                        SearchType.Repositories -> {
                                            R.string.search_tab_repositories
                                        }
                                    }
                                ),
                                color = if (pagerState.currentPage == index) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                                },
                                style = MaterialTheme.typography.body2
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = index)
                            }
                        },
                        modifier = Modifier.background(color = MaterialTheme.colors.background)
                    )
                }
            }
            Divider()
            HorizontalPager(
                state = pagerState,
                count = SearchType.values().size
            ) { page ->
                when (SearchType.values()[page]) {
                    SearchType.Users -> {
                        users?.let {
                            SearchedUsersScreen(users = it)
                        }
                    }
                    SearchType.Repositories -> {
                        repositories?.let {
                            SearchedRepositoriesScreen(repositories = it)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
private fun ItemSearchHistory(
    query: Query,
    removeQuery: (Query) -> Unit,
    clickAction: (Query) -> Unit
) {
    ListItem(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_history_24),
                contentDescription = stringResource(id = R.string.search_history)
            )
        },
        trailing = {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(id = R.string.search_history_action),
                modifier = Modifier.clickable {
                    removeQuery.invoke(query)
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { clickAction.invoke(query) })
    ) {
        Text(text = query.keyword)
    }
}

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Preview(
    name = "ItemSearchHistoryPreview",
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ItemSearchHistoryPreview(
    @PreviewParameter(
        provider = HistoryQueriesProvider::class,
        limit = 1
    )
    query: Query
) {
    ItemSearchHistory(
        query = query,
        clickAction = {},
        removeQuery = {}
    )
}