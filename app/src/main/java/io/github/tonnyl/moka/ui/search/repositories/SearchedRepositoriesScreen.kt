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
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repositories.ItemRepository
import io.github.tonnyl.moka.util.RepositoryItemProvider
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.ItemLoadingState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun SearchedRepositoriesScreen(repositories: LazyPagingItems<RepositoryItem>) {
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

@ExperimentalSerializationApi
@Composable
private fun SearchedRepositoriesScreenContent(repositories: LazyPagingItems<RepositoryItem>) {
    val repoPlaceholder = remember {
        RepositoryItemProvider().values.first()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ItemLoadingState(loadState = repositories.loadState.prepend)
        }

        if (repositories.loadState.refresh is LoadState.Loading) {
            items(count = MokaApp.defaultPagingConfig.initialLoadSize) {
                ItemRepository(
                    repo = repoPlaceholder,
                    profileType = ProfileType.USER,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(lazyPagingItems = repositories) { _, repo ->
                if (repo != null) {
                    ItemRepository(
                        repo = repo,
                        profileType = ProfileType.NOT_SPECIFIED,
                        enablePlaceholder = false
                    )
                }
            }
        }
        item {
            ItemLoadingState(loadState = repositories.loadState.append)
        }
    }
}