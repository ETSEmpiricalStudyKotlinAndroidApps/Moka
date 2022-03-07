package io.github.tonnyl.moka.ui.prs

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
import androidx.paging.ExperimentalPagingApi
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
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.IssuePrState
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.PullRequestListItem
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.PullRequestItemProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun PullRequestsScreen(
    owner: String,
    name: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val queryState = remember {
        mutableStateOf(IssuePullRequestQueryState.All)
    }

    val viewModel = viewModel(
        key = queryState.value.rawValue,
        initializer = {
            PullRequestsViewModel(
                extra = PullRequestsViewModelExtra(
                    accountInstance = currentAccount,
                    owner = owner,
                    name = name,
                    state = queryState.value
                )
            )
        }
    )

    val prs = viewModel.prsFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = prs.loadState.refresh is LoadState.Loading),
            onRefresh = prs::refresh,
            indicatorPadding = contentPaddings,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                prs.loadState.refresh is LoadState.NotLoading
                        && prs.loadState.append is LoadState.NotLoading
                        && prs.loadState.prepend is LoadState.NotLoading
                        && prs.itemCount == 0 -> {

                }
                prs.loadState.refresh is LoadState.NotLoading
                        && prs.itemCount == 0 -> {
                    EmptyScreenContent(
                        titleId = R.string.common_no_data_found,
                        action = prs::retry
                    )
                }
                prs.loadState.refresh is LoadState.Error
                        && prs.itemCount == 0 -> {
                    EmptyScreenContent(
                        action = prs::retry,
                        throwable = (prs.loadState.refresh as LoadState.Error).error
                    )
                }
                else -> {
                    PullRequestsScreenContent(
                        contentPaddings = contentPaddings,
                        owner = owner,
                        name = name,
                        prs = prs
                    )
                }
            }
        }

        val showMenuState = remember {
            mutableStateOf(false)
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.pull_requests)) },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            actions = {
                if (prs.loadState.refresh is LoadState.NotLoading
                    && prs.itemCount > 0
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

                ShareAndOpenInBrowserMenu(
                    showMenuState = remember { mutableStateOf(false) },
                    text = "https://github.com/${owner}/${name}/pulls"
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
fun PullRequestsScreenContent(
    contentPaddings: PaddingValues,
    owner: String,
    name: String,
    prs: LazyPagingItems<PullRequestListItem>,
) {
    val prPlaceholder = remember {
        PullRequestItemProvider().values.elementAt(2)
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = prs.loadState.prepend)

        if (prs.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemPullRequest(
                    owner = "TonnyL",
                    name = "PaperPlane",
                    pullRequest = prPlaceholder,
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
                    ItemPullRequest(
                        owner = owner,
                        name = name,
                        pullRequest = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        ItemLoadingState(loadState = prs.loadState.append)
    }
}

@Composable
private fun ItemPullRequest(
    owner: String,
    name: String,
    pullRequest: PullRequestListItem,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                Screen.PullRequest.navigate(
                    navController = navController,
                    login = owner,
                    repoName = name,
                    number = pullRequest.number
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            val imageRes: Int
            val contentDescriptionRes: Int
            when {
                pullRequest.state == IssuePrState.Closed
                        && pullRequest.mergedAt != null -> {
                    imageRes = R.drawable.ic_pr_merged
                    contentDescriptionRes = R.string.pr_status_merged_image_content_description
                }
                pullRequest.state == IssuePrState.Closed
                        && pullRequest.mergedAt == null -> {
                    imageRes = R.drawable.ic_pr_closed
                    contentDescriptionRes = R.string.pr_status_closed_image_content_description
                }
                else -> {
                    imageRes = R.drawable.ic_pr_open
                    contentDescriptionRes = R.string.pr_status_open_image_content_description
                }
            }
            Image(
                contentDescription = stringResource(id = contentDescriptionRes),
                painter = painterResource(id = imageRes),
                modifier = Modifier
                    .size(size = IssueTimelineEventAuthorAvatarSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Text(
                text = pullRequest.title,
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
                    text = stringResource(id = R.string.issue_pr_number, pullRequest.number),
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
                    url = pullRequest.user?.avatarUrl,
                    modifier = Modifier
                        .size(size = 24.dp)
                        .clip(shape = CircleShape)
                        .clickable(enabled = !enablePlaceholder) {
                            Screen.Profile.navigate(
                                navController = navController,
                                login = pullRequest.user?.login ?: "ghost",
                                type = ProfileType.NOT_SPECIFIED
                            )
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Text(
                    text = pullRequest.user?.login ?: "ghost",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        pullRequest.createdAt.toEpochMilliseconds(),
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
    name = "PullRequestItemPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun PullRequestItemPreview(
    @PreviewParameter(
        PullRequestItemProvider::class,
        limit = 1
    )
    pullRequest: PullRequestListItem
) {
    ItemPullRequest(
        owner = "wasabeef",
        name = "droid",
        pullRequest = pullRequest,
        enablePlaceholder = false
    )
}