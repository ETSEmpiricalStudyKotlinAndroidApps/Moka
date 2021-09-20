package io.github.tonnyl.moka.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesScreen
import io.github.tonnyl.moka.ui.search.users.SearchedUsersScreen
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.widget.SearchBar
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

private enum class SearchType {

    Repositories,

    Users,

}

@ExperimentalCoilApi
@ExperimentalPagerApi
@ExperimentalSerializationApi
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(initialSearchKeyword: String) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<SearchViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            initialSearchKeyword = initialSearchKeyword
        )
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

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SearchScreenContent(
            topAppBarSize = topAppBarSize,
            users = usersFlow?.collectAsLazyPagingItems(),
            repositories = repositoriesFlow?.collectAsLazyPagingItems()
        )

        SearchBar(
            hintResId = R.string.search_input_hint,
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagerApi
@Composable
private fun SearchScreenContent(
    topAppBarSize: Int,
    users: LazyPagingItems<SearchedUserOrOrgItem>?,
    repositories: LazyPagingItems<RepositoryItem>?
) {
    val pagerState = rememberPagerState(
        pageCount = SearchType.values().size,
        initialOffscreenLimit = 2
    )

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
        HorizontalPager(state = pagerState) { page ->
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