package io.github.tonnyl.moka.ui.pr.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.github.tonnyl.moka.ui.issue.IssueTimelineCommentItem
import io.github.tonnyl.moka.ui.issue.ItemIssueTimelineEvent
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.util.IssueTimelineEventProvider

@Composable
fun CommentTreadScreen(nodeId: String) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            CommentThreadViewModel(
                extra = CommentThreadViewModelExtra(
                    accountInstance = currentAccount,
                    nodeId = nodeId
                )
            )
        }
    )

    val thread = viewModel.threadFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = thread.loadState.refresh is LoadState.Loading),
            onRefresh = thread::refresh,
            indicatorPadding = contentPaddings,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                thread.loadState.refresh is LoadState.NotLoading
                        && thread.loadState.append is LoadState.NotLoading
                        && thread.loadState.prepend is LoadState.NotLoading
                        && thread.itemCount == 0 -> {

                }
                thread.loadState.refresh is LoadState.NotLoading
                        && thread.itemCount == 0 -> {
                    EmptyScreenContent(
                        titleId = R.string.common_no_data_found,
                        action = thread::retry
                    )
                }
                thread.loadState.refresh is LoadState.Error
                        && thread.itemCount == 0 -> {
                    EmptyScreenContent(
                        action = thread::retry,
                        throwable = (thread.loadState.refresh as LoadState.Error).error
                    )
                }
                else -> {
                    CommentThreadScreenContent(
                        contentPaddings = contentPaddings,
                        thread = thread
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.thread)) },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun CommentThreadScreenContent(
    contentPaddings: PaddingValues,
    thread: LazyPagingItems<CommentWithSimplifiedDiffHunk>
) {
    val timelinePlaceholder = remember {
        IssueTimelineEventProvider().values.first()
    }

    val enablePlaceholder = thread.loadState.refresh is LoadState.Loading

    LazyColumn(contentPadding = contentPaddings) {
        if (enablePlaceholder) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemIssueTimelineEvent(
                    event = timelinePlaceholder,
                    enablePlaceholder = enablePlaceholder
                )
            }
        } else {
            itemsIndexed(
                items = thread,
                key = { _, item ->
                    item.hashCode()
                }
            ) { _, item ->
                if (item != null) {
                    Column {
                        DiffHunk(
                            filename = item.first.path,
                            codeLines = item.second,
                            outdated = item.first.outdated
                        )
                        IssueTimelineCommentItem(
                            avatarUrl = item.first.author?.actor?.avatarUrl,
                            viewerCanReact = item.first.viewerCanReact,
                            reactionGroups = item.first.reactionGroups?.map { it.reactionGroup },
                            authorLogin = item.first.author?.actor?.login,
                            authorAssociation = item.first.authorAssociation,
                            displayHtml = item.first.bodyHTML,
                            commentCreatedAt = item.first.createdAt,
                            enablePlaceholder = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiffHunk(
    filename: String,
    codeLines: List<String>,
    outdated: Boolean
) {
    Divider(modifier = Modifier.fillMaxWidth())
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ContentPaddingLargeSize,
                vertical = ContentPaddingMediumSize
            )
    ) {
        Text(
            text = filename,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(weight = 1f)
        )

        if (outdated) {
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
            Text(
                text = stringResource(id = R.string.outdated),
                color = userStatusDndYellow,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(percent = 50))
                    .border(
                        width = DividerSize,
                        color = userStatusDndYellow,
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .padding(
                        vertical = ContentPaddingSmallSize,
                        horizontal = ContentPaddingMediumSize
                    )
            )
        }
    }
    Divider(modifier = Modifier.fillMaxWidth())
    codeLines.forEachIndexed { index, code ->
        val verticalPadding = if (index == 0 || index == codeLines.size - 1) {
            ContentPaddingMediumSize
        } else {
            ContentPaddingSmallSize
        }
        Text(
            text = code,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when {
                        code.startsWith("@@") -> {
                            commitModificationColor
                        }
                        code.startsWith("+") -> {
                            commitAdditionColor
                        }
                        else -> {
                            commitDeletionColor
                        }
                    }.copy(alpha = .4f)
                )
                .padding(
                    start = ContentPaddingLargeSize,
                    end = ContentPaddingLargeSize,
                    top = verticalPadding,
                    bottom = verticalPadding
                )
        )
    }
}

@Preview(
    name = "DiffHunkPreview",
    showBackground = true,
    backgroundColor = 0xffffff
)
@Composable
private fun DiffHunkPreview() {
    Column {
        DiffHunk(
            filename = "kotlinx-coroutines-core/jvm/src/RateLimiter.kt",
            codeLines = listOf(
                "+     */",
                "+    override suspend fun acquire() {",
                "+        val now: Long = System.currentTimeMillis()",
                "+        val delay: Long = delaySequence.next()"
            ),
            outdated = true
        )
    }
}