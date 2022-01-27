package io.github.tonnyl.moka.ui.releases

import android.text.format.DateUtils
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
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
import io.github.tonnyl.moka.ui.releases.ReleasesViewModel.Companion.RELEASES_VIEW_MODEL_EXTRA_KEY
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.ReleaseListItemProvider
import io.tonnyl.moka.graphql.fragment.ReleaseListItem
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun ReleasesScreen(
    login: String,
    repoName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<ReleasesViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[RELEASES_VIEW_MODEL_EXTRA_KEY] = ReleasesViewModelExtra(
                accountInstance = currentAccount,
                login = login,
                repoName = repoName
            )
        }
    )

    val releases = viewModel.releasesFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = releases.loadState.refresh is LoadState.Loading),
            onRefresh = releases::refresh,
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
                releases.loadState.refresh is LoadState.NotLoading
                        && releases.loadState.append is LoadState.NotLoading
                        && releases.loadState.prepend is LoadState.NotLoading
                        && releases.itemCount == 0 -> {

                }
                releases.loadState.refresh is LoadState.NotLoading
                        && releases.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                releases.loadState.refresh is LoadState.Error
                        && releases.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    ReleasesScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        releases = releases,
                        login = login,
                        repoName = repoName
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.releases))
            },
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
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun ReleasesScreenContent(
    contentTopPadding: Dp,
    login: String,
    repoName: String,
    releases: LazyPagingItems<ReleaseListItem>
) {
    val releasePlaceholder = remember {
        ReleaseListItemProvider().values.first()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = releases.loadState.prepend)
        }

        val isInitialLoading = releases.loadState.refresh is LoadState.Loading
        if (isInitialLoading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemRelease(
                    login = login,
                    repoName = repoName,
                    release = releasePlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            items(
                items = releases,
                key = { it.url }
            ) { item ->
                if (item != null) {
                    ItemRelease(
                        login = login,
                        repoName = repoName,
                        release = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = releases.loadState.append)
        }
    }
}

@Composable
private fun ItemRelease(
    login: String,
    repoName: String,
    release: ReleaseListItem,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                navController.navigate(
                    route = Screen.Release.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                        .replace("{${Screen.ARG_TAG_NAME}}", release.tagName)
                )
            }
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        Text(
            text = release.name.takeIf { !it.isNullOrEmpty() } ?: release.tagName,
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
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        release.createdAt.toEpochMilliseconds(),
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
                if (release.isPrerelease
                    || release.isLatest
                    || release.isDraft
                ) {
                    if (enablePlaceholder) {
                        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                    } else {
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Box(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .size(size = ContentPaddingSmallSize)
                                .background(
                                    color = MaterialTheme.colors.onBackground.copy(
                                        ContentAlpha.medium
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    }
                    Text(
                        text = stringResource(
                            id = when {
                                release.isLatest -> {
                                    R.string.release_is_latest
                                }
                                release.isPrerelease -> {
                                    R.string.release_is_pre_release
                                }
                                else -> {
                                    R.string.release_is_draft
                                }
                            }
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2,
                        color = if (release.isLatest) {
                            issuePrGreen
                        } else {
                            userStatusDndYellow
                        },
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        }
    }
}

@Preview(
    name = "ItemReleasePreview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ItemReleasePreview(
    @PreviewParameter(
        provider = ReleaseListItemProvider::class,
        limit = 1
    )
    release: ReleaseListItem
) {
    ItemRelease(
        login = "TonnyL",
        repoName = "PaperPlane",
        release = release,
        enablePlaceholder = false
    )
}