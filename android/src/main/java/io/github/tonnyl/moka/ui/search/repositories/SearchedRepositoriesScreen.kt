package io.github.tonnyl.moka.ui.search.repositories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.github.tonnyl.moka.ui.repositories.ItemRepository
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.util.RepositoryItemProvider
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment

@Composable
fun SearchedRepositoriesScreen(repositories: LazyPagingItems<RepositoryListItemFragment>) {
    val contentPaddings = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = repositories.loadState.refresh is LoadState.Loading),
        onRefresh = repositories::refresh,
        indicator = { state, refreshTriggerDistance ->
            DefaultSwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTriggerDistance
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            repositories.loadState.refresh is LoadState.NotLoading
                    && repositories.loadState.append is LoadState.NotLoading
                    && repositories.loadState.prepend is LoadState.NotLoading
                    && repositories.itemCount == 0 -> {

            }
            repositories.loadState.refresh is LoadState.NotLoading
                    && repositories.itemCount == 0 -> {
                EmptyScreenContent(
                    titleId = R.string.common_no_data_found,
                    action = repositories::retry
                )
            }
            repositories.loadState.refresh is LoadState.Error
                    && repositories.itemCount == 0 -> {
                EmptyScreenContent(
                    action = repositories::retry,
                    throwable = (repositories.loadState.refresh as LoadState.Error).error
                )
            }
            else -> {
                SearchedRepositoriesScreenContent(
                    contentPaddings = contentPaddings,
                    repositories = repositories
                )
            }
        }
    }
}

@Composable
private fun SearchedRepositoriesScreenContent(
    contentPaddings: PaddingValues,
    repositories: LazyPagingItems<RepositoryListItemFragment>
) {
    val repoPlaceholder = remember {
        RepositoryItemProvider().values.first()
    }

    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = repositories.loadState.prepend)

        if (repositories.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemRepository(
                    repo = repoPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = repositories,
                key = { _, item ->
                    item.id
                }
            ) { _, repo ->
                if (repo != null) {
                    ItemRepository(
                        repo = repo,
                        enablePlaceholder = false
                    )
                }
            }
        }

        ItemLoadingState(loadState = repositories.loadState.append)
    }
}