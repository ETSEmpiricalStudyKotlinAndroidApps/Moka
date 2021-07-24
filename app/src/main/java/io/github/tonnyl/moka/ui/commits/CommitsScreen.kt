package io.github.tonnyl.moka.ui.commits

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.fragment.CommitListItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery.Data.User.Repository.DefaultBranchRef.CommitTarget.History.Node
import io.github.tonnyl.moka.type.StatusState
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.CommitProvider
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun CommitsScreen(
    isOrg: Boolean,
    login: String,
    repoName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<CommitsViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            isOrg = isOrg,
            login = login,
            repoName = repoName
        )
    )

    val commitsPager = remember { viewModel.commitsFlow }
    val commits = commitsPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = commits.loadState.refresh is LoadState.Loading),
            onRefresh = commits::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                commits.loadState.refresh is LoadState.NotLoading
                        && commits.loadState.append is LoadState.NotLoading
                        && commits.loadState.prepend is LoadState.NotLoading
                        && commits.itemCount == 0 -> {

                }
                commits.loadState.refresh is LoadState.NotLoading
                        && commits.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                commits.loadState.refresh is LoadState.Error
                        && commits.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    CommitsScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        commits = commits
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.commits))
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_arrow_back_24)
                        )
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun CommitsScreenContent(
    contentTopPadding: Dp,
    commits: LazyPagingItems<CommitListItem>
) {
    val commitPlaceholder = remember {
        CommitProvider().values.first()
    }
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = commits.loadState.prepend)
        }

        val isInitialLoading = commits.loadState.refresh is LoadState.Loading
        if (isInitialLoading) {
            items(count = MokaApp.defaultPagingConfig.initialLoadSize) {
                ItemCommit(
                    commit = commitPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            items(lazyPagingItems = commits) { item ->
                if (item != null) {
                    ItemCommit(
                        commit = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = commits.loadState.append)
        }
    }
}

@Composable
private fun ItemCommit(
    commit: CommitListItem,
    enablePlaceholder: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) { }
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = commit.messageHeadline,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
            Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(
                        data = commit.author?.avatarUrl ?: commit.committer?.user?.avatarUrl,
                        builder = {
                            createAvatarLoadRequest()
                        }
                    ),
                    contentDescription = stringResource(id = R.string.accounts_avatar_of_account),
                    modifier = Modifier
                        .size(size = IssueTimelineEventAuthorAvatarSize)
                        .clip(shape = CircleShape)
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                Text(
                    text = commit.author?.user?.login ?: commit.committer?.user?.login ?: "ghost",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(
                            commit.committedDate.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ).toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        }
        if (commit.statusCheckRollup?.state == StatusState.SUCCESS
            || commit.statusCheckRollup?.state == StatusState.FAILURE
        ) {
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))

            val isSuccess = commit.statusCheckRollup?.state == StatusState.SUCCESS
            Image(
                painter = painterResource(
                    id = if (isSuccess) {
                        R.drawable.ic_check_24
                    } else {
                        R.drawable.ic_close_24
                    }
                ),
                contentDescription = stringResource(
                    id = if (isSuccess) {
                        R.string.commit_status_success
                    } else {
                        R.string.commit_status_failure
                    }
                ),
                colorFilter = ColorFilter.tint(
                    color = if (isSuccess) {
                        issuePrGreen
                    } else {
                        MaterialTheme.colors.error
                    }
                ),
                modifier = Modifier.placeholder(visible = enablePlaceholder)
            )
        }
    }
}

@Preview(name = "ItemCommitPreview", showBackground = true)
@Composable
private fun ItemCommitPreview(
    @PreviewParameter(
        provider = CommitProvider::class,
        limit = 1
    )
    commit: Node
) {
    ItemCommit(
        commit = commit,
        enablePlaceholder = false
    )
}