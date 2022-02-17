package io.github.tonnyl.moka.ui.topics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.topics.RepositoryTopicsViewModel.Companion.REPOSITORY_TOPICS_VIEW_MODEL_EXTRA_KEY
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.RepositoryTopicProvider
import io.tonnyl.moka.graphql.fragment.RepositoryTopic
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun RepositoryTopicsScreen(
    login: String,
    repoName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<RepositoryTopicsViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[REPOSITORY_TOPICS_VIEW_MODEL_EXTRA_KEY] = RepositoryTopicsViewModelExtra(
                accountInstance = currentAccount,
                login = login,
                repoName = repoName
            )
        }
    )

    val topics = viewModel.topicsFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = topics.loadState.refresh is LoadState.Loading),
            onRefresh = topics::refresh,
            indicatorPadding = contentPaddings,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                topics.loadState.refresh is LoadState.NotLoading
                        && topics.loadState.append is LoadState.NotLoading
                        && topics.loadState.prepend is LoadState.NotLoading
                        && topics.itemCount == 0 -> {

                }
                topics.loadState.refresh is LoadState.NotLoading
                        && topics.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                topics.loadState.refresh is LoadState.Error
                        && topics.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    RepositoriesScreenContent(
                        contentPaddings = contentPaddings,
                        topics = topics
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.repository_topics))
            },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
private fun RepositoriesScreenContent(
    contentPaddings: PaddingValues,
    topics: LazyPagingItems<RepositoryTopic>
) {
    val topicPlaceholder = remember {
        RepositoryTopicProvider().values.first()
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = topics.loadState.prepend)

        if (topics.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemTopic(
                    topic = topicPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            items(
                items = topics,
                key = { it.id }
            ) { topic ->
                if (topic != null) {
                    ItemTopic(
                        topic = topic,
                        enablePlaceholder = false
                    )
                }
            }
        }

        ItemLoadingState(loadState = topics.loadState.append)
    }
}

@ExperimentalMaterialApi
@Composable
private fun ItemTopic(
    topic: RepositoryTopic,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current
    val topicName = topic.topic.topic.name
    if (topicName.isNotEmpty()) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !enablePlaceholder) {
                    Screen.Search.navigate(
                        navController = navController,
                        keyword = topicName
                    )
                }
        ) {
            Text(
                text = topicName,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(name = "ItemTopicPreview", showBackground = true)
@Composable
private fun ItemTopicPreview(
    @PreviewParameter(
        provider = RepositoryTopicProvider::class,
        limit = 1
    )
    topic: RepositoryTopic
) {
    ItemTopic(
        topic = topic,
        enablePlaceholder = false
    )
}