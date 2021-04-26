package io.github.tonnyl.moka.ui.search.repositories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repositories.ItemRepository
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.github.tonnyl.moka.widget.Pager
import io.github.tonnyl.moka.widget.PagerState

@Composable
fun SearchedRepositoriesScreen(
    repositories: LazyPagingItems<RepositoryItem>,
    pagerState: PagerState = remember { PagerState() }
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = repositories.loadState.refresh is LoadState.Loading),
        onRefresh = repositories::refresh
    ) {
        Pager(
            state = pagerState,
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
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                repositories.loadState.refresh is LoadState.Error
                        && repositories.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    SearchedRepositoriesScreenContent(repositories = repositories)
                }
            }
        }
    }
}

@Composable
private fun SearchedRepositoriesScreenContent(repositories: LazyPagingItems<RepositoryItem>) {
    LazyColumn {
        item {
            ItemLoadingState(loadState = repositories.loadState.prepend)
        }
        itemsIndexed(lazyPagingItems = repositories) { _, repo ->
            if (repo != null) {
                ItemRepository(
                    repo = repo,
                    profileType = ProfileType.NOT_SPECIFIED
                )
            }
        }
        item {
            ItemLoadingState(loadState = repositories.loadState.append)
        }
    }
}