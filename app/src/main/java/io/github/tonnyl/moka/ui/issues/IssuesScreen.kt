package io.github.tonnyl.moka.ui.issues

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.util.IssueItemProvider
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.github.tonnyl.moka.widget.SwipeToRefreshLayout

@Composable
fun IssuesScreen(
    owner: String,
    name: String,
    navController: NavController
) {
    val viewModel = viewModel<IssuesViewModel>(
        factory = ViewModelFactory(owner = owner, name = name)
    )

    val issuesFlow = remember {
        viewModel.issuesFlow
    }
    val issues = issuesFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SwipeToRefreshLayout(
            refreshingState = issues.loadState.refresh is LoadState.Loading,
            onRefresh = issues::refresh,
            refreshIndicator = {
                Surface(
                    elevation = 10.dp,
                    shape = CircleShape
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(size = 36.dp)
                            .padding(all = ContentPaddingSmallSize)
                    )
                }
            }
        ) {
            when {
                issues.loadState.refresh is LoadState.NotLoading
                        && issues.loadState.append is LoadState.NotLoading
                        && issues.loadState.prepend is LoadState.NotLoading
                        && issues.itemCount == 0 -> {

                }
                issues.loadState.refresh is LoadState.NotLoading
                        && issues.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                issues.loadState.refresh is LoadState.Error
                        && issues.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    IssuesScreenContent(
                        topAppBarSize = topAppBarSize,
                        owner = owner,
                        name = name,
                        navController = navController,
                        prs = issues
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.issues)) },
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

@Composable
fun IssuesScreenContent(
    topAppBarSize: Int,
    owner: String,
    name: String,
    navController: NavController,
    prs: LazyPagingItems<IssueItem>,
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            ItemLoadingState(loadState = prs.loadState.prepend)
        }

        itemsIndexed(lazyPagingItems = prs) { index, item ->
            if (item != null) {
                ItemIssue(
                    owner = owner,
                    name = name,
                    issue = item,
                    navController = navController
                )
            }
        }

        item {
            ItemLoadingState(loadState = prs.loadState.append)
        }
    }
}

@Composable
private fun ItemIssue(
    owner: String,
    name: String,
    issue: IssueItem,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.navigate(
                    route = Screen.Issue.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", owner)
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", name)
                        .replace("{${Screen.ARG_ISSUE_PR_NUMBER}}", issue.number.toString())
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Image(
                contentDescription = stringResource(
                    id = if (issue.closed) {
                        R.string.issue_status_closed_image_content_description
                    } else {
                        R.string.issue_status_open_image_content_description
                    }
                ),
                painter = painterResource(
                    id = R.drawable.ic_issue_closed_24.takeIf {
                        issue.closed
                    } ?: R.drawable.ic_issue_open_24
                ),
                modifier = Modifier.size(size = IssueTimelineEventAuthorAvatarSize)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Text(
                text = issue.title,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(weight = 1f)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.issue_pr_number, issue.number),
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 40.dp)
            ) {
                CoilImage(
                    contentDescription = stringResource(id = R.string.users_avatar_content_description),
                    request = createAvatarLoadRequest(url = issue.actor?.avatarUrl),
                    modifier = Modifier
                        .size(size = 24.dp)
                        .clip(shape = CircleShape)
                        .clickable {

                        }
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Text(
                    text = issue.actor?.login ?: "ghost",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        issue.createdAt.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Preview(name = "IssueItemPreview", showBackground = true)
@Composable
private fun IssueItemPreview(
    @PreviewParameter(
        provider = IssueItemProvider::class,
        limit = 1
    )
    issue: IssueItem
) {
    ItemIssue(
        owner = "wasabeef",
        name = "droid",
        navController = rememberNavController(),
        issue = issue
    )
}