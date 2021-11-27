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
import coil.annotation.ExperimentalCoilApi
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
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.CommitProvider
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.graphql.fragment.CommitListItem
import io.tonnyl.moka.graphql.type.StatusState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
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
    val viewModel = viewModel<CommitsViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            login = login,
            repoName = repoName,
            qualifiedName = qualifiedName
        ),
        key = qualifiedName
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
            actions = {
                TextButton(
                    onClick = {
                        navController.navigate(
                            route = Screen.Branches.route
                                .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                .replace("{${Screen.ARG_REF_PREFIX}}", refPrefix)
                                .replace(
                                    "{${Screen.ARG_DEFAULT_BRANCH_NAME}}",
                                    defaultBranchName
                                )
                                .replace("{${Screen.ARG_SELECTED_BRANCH_NAME}}", selectedBranchName)
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
private fun CommitsScreenContent(
    contentTopPadding: Dp,
    commits: LazyPagingItems<CommitListItem>,
    login: String,
    repoName: String
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

        item {
            ItemLoadingState(loadState = commits.loadState.append)
        }
    }
}

@ExperimentalCoilApi
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
                navController.navigate(
                    route = Screen.Commit.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                        .replace("{${Screen.ARG_REF}}", commit.oid)
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
                Image(
                    painter = rememberImagePainter(
                        data = commit.author?.gitActorListItem?.avatarUrl
                            ?: commit.committer?.gitActorListItem?.user?.userListItemFragment?.avatarUrl,
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

@ExperimentalCoilApi
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