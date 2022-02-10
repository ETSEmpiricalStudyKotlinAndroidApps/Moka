package io.github.tonnyl.moka.ui.prs

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
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
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.IssuePrState
import io.tonnyl.moka.common.data.IssuePullRequestQueryState
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

    val viewModel = viewModel<PullRequestsViewModel>(
        key = queryState.value.rawValue,
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[PullRequestsViewModel.PULL_REQUESTS_VIEW_MODEL_EXTRA_KEY] =
                PullRequestsViewModelExtra(
                    accountInstance = currentAccount,
                    owner = owner,
                    name = name,
                    state = queryState.value
                )
        }
    )

    val prs = viewModel.prsFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = prs.loadState.refresh is LoadState.Loading),
            onRefresh = prs::refresh,
            indicatorPadding = contentPadding,
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
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                prs.loadState.refresh is LoadState.Error
                        && prs.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    PullRequestsScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        owner = owner,
                        name = name,
                        prs = prs
                    )
                }
            }
        }

        val navController = LocalNavController.current

        val showMenuState = remember {
            mutableStateOf(false)
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.pull_requests)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            imageVector = Icons.Outlined.ArrowBack
                        )
                    }
                )
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
    contentTopPadding: Dp,
    owner: String,
    name: String,
    prs: LazyPagingItems<PullRequestListItem>,
) {
    val prPlaceholder = remember {
        PullRequestItemProvider().values.elementAt(2)
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

        item {
            ItemLoadingState(loadState = prs.loadState.append)
        }
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
                Image(
                    painter = rememberImagePainter(
                        data = pullRequest.user?.avatarUrl,
                        builder = {
                            createAvatarLoadRequest()
                        }
                    ),
                    contentDescription = stringResource(id = R.string.users_avatar_content_description),
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