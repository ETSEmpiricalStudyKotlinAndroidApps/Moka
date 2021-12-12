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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.IssueListItem
import io.tonnyl.moka.common.data.IssuePrState
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.IssueItemProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun IssuesScreen(
    owner: String,
    name: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val queryState = remember {
        mutableStateOf(IssuePullRequestQueryState.All)
    }

    val viewModel = viewModel<IssuesViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            owner = owner,
            name = name,
            queryState = queryState.value
        ),
        key = queryState.value.rawValue
    )

    val issues = viewModel.issuesFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

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
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        owner = owner,
                        name = name,
                        prs = issues
                    )
                }
            }
        }

        val navController = LocalNavController.current

        val showMenuState = remember {
            mutableStateOf(false)
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
            actions = {
                if (issues.loadState.refresh is LoadState.NotLoading
                    && issues.itemCount > 0
                ) {
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
fun IssuesScreenContent(
    contentTopPadding: Dp,
    owner: String,
    name: String,
    prs: LazyPagingItems<IssueListItem>,
) {
    val issuePlaceholder = remember {
        IssueItemProvider().values.elementAt(1)
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = prs.loadState.prepend)
        }

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

        item {
            ItemLoadingState(loadState = prs.loadState.append)
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun ItemIssue(
    owner: String,
    name: String,
    issue: IssueListItem,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    val navigateToProfile = {
        navController.navigate(
            route = Screen.Profile.route
                .replace("{${Screen.ARG_PROFILE_LOGIN}}", owner)
                .replace(
                    "{${Screen.ARG_PROFILE_TYPE}}",
                    ProfileType.NOT_SPECIFIED.name
                )
        )
    }

    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
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
                Image(
                    painter = rememberImagePainter(
                        data = issue.user?.avatarUrl,
                        builder = {
                            createAvatarLoadRequest()
                        }
                    ),
                    contentDescription = stringResource(id = R.string.users_avatar_content_description),
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

@ExperimentalCoilApi
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