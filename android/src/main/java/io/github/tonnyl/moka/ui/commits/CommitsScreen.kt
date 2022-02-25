package io.github.tonnyl.moka.ui.commits

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
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
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.CommitProvider
import io.tonnyl.moka.graphql.fragment.CommitListItem
import io.tonnyl.moka.graphql.type.StatusState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun CommitsScreen(
    login: String,
    repoName: String,
    refPrefix: String,
    defaultBranchName: String,
    selectedBranchName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val qualifiedName = "${refPrefix}/${selectedBranchName}"
    val viewModel = viewModel(
        initializer = {
            CommitsViewModel(
                CommitsViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repoName = repoName,
                    qualifiedName = qualifiedName
                )
            )
        },
        key = qualifiedName
    )

    val commits = viewModel.commitsFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = commits.loadState.refresh is LoadState.Loading),
            onRefresh = commits::refresh,
            indicatorPadding = contentPaddings,
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
                        titleId = R.string.common_no_data_found,
                        action = commits::retry
                    )
                }
                commits.loadState.refresh is LoadState.Error
                        && commits.itemCount == 0 -> {
                    EmptyScreenContent(action = commits::retry)
                }
                else -> {
                    CommitsScreenContent(
                        contentPaddings = contentPaddings,
                        commits = commits,
                        login = login,
                        repoName = repoName
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
                AppBarNavigationIcon()
            },
            actions = {
                TextButton(
                    onClick = {
                        Screen.Branches.navigate(
                            navController = navController,
                            login = login,
                            repoName = repoName,
                            refPrefix = refPrefix,
                            defaultBranchName = defaultBranchName,
                            selectedBranchName = selectedBranchName
                        )
                    }
                ) {
                    Text(
                        text = selectedBranchName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
    contentPaddings: PaddingValues,
    commits: LazyPagingItems<CommitListItem>,
    login: String,
    repoName: String
) {
    val commitPlaceholder = remember {
        CommitProvider().values.first()
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = commits.loadState.prepend)

        val isInitialLoading = commits.loadState.refresh is LoadState.Loading
        if (isInitialLoading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemCommit(
                    commit = commitPlaceholder,
                    enablePlaceholder = true,
                    login = login,
                    repoName = repoName
                )
            }
        } else {
            items(
                items = commits,
                key = { it.oid }
            ) { item ->
                if (item != null) {
                    ItemCommit(
                        commit = item,
                        enablePlaceholder = false,
                        login = login,
                        repoName = repoName
                    )
                }
            }
        }

        ItemLoadingState(loadState = commits.loadState.append)
    }
}

@Composable
private fun ItemCommit(
    commit: CommitListItem,
    enablePlaceholder: Boolean,
    login: String,
    repoName: String
) {
    val navController = LocalNavController.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                Screen.Commit.navigate(
                    navController = navController,
                    login = login,
                    repoName = repoName,
                    ref = commit.oid
                )
            }
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
                AvatarImage(
                    url = commit.author?.gitActorListItem?.avatarUrl
                        ?: commit.committer?.gitActorListItem?.user?.userListItemFragment?.avatarUrl,
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
                    text = commit.author?.gitActorListItem?.user?.userListItemFragment?.login
                        ?: commit.committer?.gitActorListItem?.user?.userListItemFragment?.login
                        ?: "ghost",
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
            CommitVerification(
                verified = isSuccess,
                enablePlaceholder = enablePlaceholder
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
    commit: CommitListItem
) {
    ItemCommit(
        commit = commit,
        enablePlaceholder = false,
        login = "",
        repoName = ""
    )
}