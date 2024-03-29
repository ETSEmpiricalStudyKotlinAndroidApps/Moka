package io.github.tonnyl.moka.ui.issues

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.IssueListItem
import io.tonnyl.moka.common.data.IssuePrState
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.util.IssueItemProvider

@Composable
fun IssuesScreen(
    owner: String,
    name: String,
    repoId: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val queryState = remember {
        mutableStateOf(IssuePullRequestQueryState.All)
    }

    val viewModel = viewModel(
        initializer = {
            IssuesViewModel(
                extra = IssuesViewModelExtra(
                    accountInstance = currentAccount,
                    owner = owner,
                    name = name,
                    repoId = repoId,
                    queryState = queryState.value
                )
            )
        },
        key = queryState.value.rawValue
    )

    val issues = viewModel.issuesFlow.collectAsLazyPagingItems()

    val isLoadingFinished = issues.loadState.refresh is LoadState.NotLoading
            && issues.itemCount > 0

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        val navController = LocalNavController.current

        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        Screen.CreateIssue.navigate(
                            navController = navController,
                            repoId = repoId
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        tint = MaterialTheme.colors.onSecondary,
                        contentDescription = stringResource(id = R.string.create_issue)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            val contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = issues.loadState.refresh is LoadState.Loading),
                onRefresh = issues::refresh,
                indicatorPadding = contentPadding,
                indicator = { state, refreshTriggerDistance ->
                    DefaultSwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance
                    )
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
                            titleId = R.string.common_no_data_found,
                            action = issues::retry
                        )
                    }
                    issues.loadState.refresh is LoadState.Error
                            && issues.itemCount == 0 -> {
                        EmptyScreenContent(
                            action = issues::retry,
                            throwable = (issues.loadState.refresh as LoadState.Error).error
                        )
                    }
                    else -> {
                        IssuesScreenContent(
                            contentPaddings = contentPadding,
                            owner = owner,
                            name = name,
                            prs = issues
                        )
                    }
                }
            }
        }

        val showMenuState = remember {
            mutableStateOf(false)
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.issues)) },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            actions = {
                if (isLoadingFinished) {
                    Box {
                        IconButton(
                            onClick = {
                                showMenuState.value = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter_24),
                                contentDescription = stringResource(id = R.string.notification_filters)
                            )
                        }
                        IssuePrFiltersDropdownMenu(
                            showMenu = showMenuState,
                            queryState = queryState
                        )
                    }
                }

                ShareAndOpenInBrowserMenu(
                    showMenuState = remember { mutableStateOf(false) },
                    text = "https://github.com/${owner}/${name}/issues"
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
    contentPaddings: PaddingValues,
    owner: String,
    name: String,
    prs: LazyPagingItems<IssueListItem>,
) {
    val issuePlaceholder = remember {
        IssueItemProvider().values.elementAt(1)
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = prs.loadState.prepend)

        if (prs.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemIssue(
                    owner = "TonnyL",
                    name = "PaperPlane",
                    issue = issuePlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = prs,
                key = { _, item ->
                    item.id
                }
            ) { _, item ->
                if (item != null) {
                    ItemIssue(
                        owner = owner,
                        name = name,
                        issue = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        ItemLoadingState(loadState = prs.loadState.append)
    }
}

@Composable
private fun ItemIssue(
    owner: String,
    name: String,
    issue: IssueListItem,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    val navigateToProfile = {
        Screen.Profile.navigate(
            navController = navController,
            login = owner,
            type = ProfileType.NOT_SPECIFIED
        )
    }

    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                Screen.Issue.navigate(
                    navController = navController,
                    login = owner,
                    repoName = name,
                    number = issue.number
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Image(
                contentDescription = stringResource(
                    id = if (issue.state == IssuePrState.Closed) {
                        R.string.issue_status_closed_image_content_description
                    } else {
                        R.string.issue_status_open_image_content_description
                    }
                ),
                painter = painterResource(
                    id = R.drawable.ic_issue_closed_24.takeIf {
                        issue.state == IssuePrState.Closed
                    } ?: R.drawable.ic_issue_open_24
                ),
                modifier = Modifier
                    .size(size = IssueTimelineEventAuthorAvatarSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Text(
                text = issue.title,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 1f)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.issue_pr_number, issue.number),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 40.dp)
            ) {
                AvatarImage(
                    url = issue.user?.avatarUrl,
                    modifier = Modifier
                        .size(size = 24.dp)
                        .clip(shape = CircleShape)
                        .clickable(enabled = !enablePlaceholder) {
                            navigateToProfile.invoke()
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Text(
                    text = issue.user?.login ?: "ghost",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(enabled = !enablePlaceholder) {
                            navigateToProfile.invoke()
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
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
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
        }
    }
}

@Preview(
    name = "IssueItemPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun IssueItemPreview(
    @PreviewParameter(
        provider = IssueItemProvider::class,
        limit = 1
    )
    issue: IssueListItem
) {
    ItemIssue(
        owner = "wasabeef",
        name = "droid",
        issue = issue,
        enablePlaceholder = false
    )
}