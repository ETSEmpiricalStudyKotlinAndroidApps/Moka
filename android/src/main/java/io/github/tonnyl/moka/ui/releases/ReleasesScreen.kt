package io.github.tonnyl.moka.ui.releases

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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

    val viewModel = viewModel(
        initializer = {
            ReleasesViewModel(
                extra = ReleasesViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repoName = repoName
                )
            )
        }
    )

    val releases = viewModel.releasesFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = releases.loadState.refresh is LoadState.Loading),
            onRefresh = releases::refresh,
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
                releases.loadState.refresh is LoadState.NotLoading
                        && releases.loadState.append is LoadState.NotLoading
                        && releases.loadState.prepend is LoadState.NotLoading
                        && releases.itemCount == 0 -> {

                }
                releases.loadState.refresh is LoadState.NotLoading
                        && releases.itemCount == 0 -> {
                    EmptyScreenContent(
                        titleId = R.string.common_no_data_found,
                        action = releases::retry
                    )
                }
                releases.loadState.refresh is LoadState.Error
                        && releases.itemCount == 0 -> {
                    EmptyScreenContent(
                        action = releases::retry,
                        throwable = (releases.loadState.refresh as LoadState.Error).error
                    )
                }
                else -> {
                    ReleasesScreenContent(
                        contentPaddings = contentPaddings,
                        releases = releases,
                        login = login,
                        repoName = repoName
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.releases))
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
@Composable
private fun ReleasesScreenContent(
    contentPaddings: PaddingValues,
    login: String,
    repoName: String,
    releases: LazyPagingItems<ReleaseListItem>
) {
    val releasePlaceholder = remember {
        ReleaseListItemProvider().values.first()
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = releases.loadState.prepend)

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

        ItemLoadingState(loadState = releases.loadState.append)
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
                Screen.Release.navigate(
                    navController = navController,
                    login = login,
                    repoName = repoName,
                    tagName = release.tagName
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