package io.github.tonnyl.moka.ui.commit

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.fadeHighlightColor
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.CommitFile
import io.tonnyl.moka.common.data.CommitResponse
import io.tonnyl.moka.common.extensions.orGhostAvatarUrl
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.CommitFileProvider
import io.tonnyl.moka.common.util.CommitResponseProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun CommitScreen(
    owner: String,
    repo: String,
    ref: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<CommitViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[CommitViewModel.COMMIT_VIEW_MODEL_EXTRA_KEY] = CommitViewModelExtra(
                accountInstance = currentAccount,
                owner = owner,
                repo = repo,
                ref = ref
            )
        }
    )

    val files = viewModel.commitFilesFlow.collectAsLazyPagingItems()

    val commitResp by viewModel.commitResp.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = files.loadState.refresh is LoadState.Loading),
            onRefresh = files::refresh,
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
                files.loadState.refresh is LoadState.NotLoading
                        && files.loadState.append is LoadState.NotLoading
                        && files.loadState.prepend is LoadState.NotLoading
                        && files.itemCount == 0 -> {

                }
                files.loadState.refresh is LoadState.NotLoading
                        && files.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                files.loadState.refresh is LoadState.Error
                        && files.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    CommitScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        files = files,
                        commitResp = commitResp
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.commit))
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
@ExperimentalCoilApi
@Composable
private fun CommitScreenContent(
    contentTopPadding: Dp,
    files: LazyPagingItems<CommitFile>,
    commitResp: CommitResponse?
) {
    val commitRespPlaceholder = remember {
        CommitResponseProvider().values.first()
    }
    val commitFilePlaceholder = remember {
        CommitFileProvider().values.first()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = files.loadState.prepend)
        }

        val isInitialLoading = files.loadState.refresh is LoadState.Loading
        item {
            if (isInitialLoading) {
                ItemCommitDetails(
                    resp = commitRespPlaceholder,
                    enablePlaceholder = isInitialLoading
                )
            } else if (commitResp != null) {
                ItemCommitDetails(
                    resp = commitResp,
                    enablePlaceholder = isInitialLoading
                )
            }
        }

        if (isInitialLoading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                CommitFileItem(
                    file = commitFilePlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            items(items = files) { item ->
                if (item != null) {
                    CommitFileItem(
                        file = item,
                        enablePlaceholder = isInitialLoading
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = files.loadState.append)
        }
    }
}

@Composable
private fun CommitFileItem(
    file: CommitFile,
    enablePlaceholder: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Divider()
        Text(
            text = file.filename,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(
                    horizontal = ContentPaddingLargeSize,
                    vertical = ContentPaddingMediumSize
                )
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Divider()
        val annotatedString = buildDiffAnnotatedString(file.patch)
        if (!annotatedString.isNullOrEmpty()) {
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(all = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        } else {
            Text(
                text = stringResource(id = R.string.commit_binary_file_not_shown),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(red = 246, green = 248, blue = 250))
                    .padding(all = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
    }
}

@Composable
private fun buildDiffAnnotatedString(patch: String?): AnnotatedString? {
    if (patch == null) {
        return null
    }

    val multiLines = patch.split(regex = "\\r?\\n|\\r".toRegex())
    if (multiLines.isEmpty()) {
        return null
    }

    return buildAnnotatedString {
        multiLines.forEach { s ->
            when {
                s.startsWith("@@") -> {
                    append(
                        text = AnnotatedString(
                            text = s,
                            spanStyle = SpanStyle(color = commitModificationColor)
                        )
                    )
                }
                s.getOrNull(0) == '+' -> {
                    append(
                        text = AnnotatedString(
                            text = s,
                            spanStyle = SpanStyle(color = commitAdditionColor)
                        )
                    )
                }
                s.getOrNull(0) == '-' -> {
                    append(
                        text = AnnotatedString(
                            text = s,
                            spanStyle = SpanStyle(color = commitDeletionColor)
                        )
                    )
                }
                else -> {
                    append(text = s)
                }
            }
            append("\n")
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun ItemCommitDetails(
    resp: CommitResponse,
    enablePlaceholder: Boolean
) {
    Column(
        modifier = Modifier
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        Text(
            text = resp.commit.message,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
        Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(
                    data = resp.author?.avatarUrl.orGhostAvatarUrl,
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
                text = resp.author?.login ?: "ghost",
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
                        resp.commit.committer.date.toEpochMilliseconds(),
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
            if (resp.commit.verification != null) {
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))

                CommitVerification(
                    verified = resp.commit.verification!!.verified,
                    enablePlaceholder = enablePlaceholder
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        Text(
            text = resp.sha,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        Row {
            Text(
                text = LocalContext.current.resources.getQuantityString(
                    R.plurals.commit_changed_files,
                    resp.files.size,
                    resp.files.size
                ),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
            Text(
                text = LocalContext.current.resources.getQuantityString(
                    R.plurals.commit_with_additions,
                    resp.stats.additions.toInt(),
                    resp.stats.additions,
                ),
                style = MaterialTheme.typography.body2,
                color = commitAdditionColor,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade(
                        highlightColor = PlaceholderDefaults.fadeHighlightColor(
                            backgroundColor = commitAdditionColor
                        )
                    )
                )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
            Text(
                text = LocalContext.current.resources.getQuantityString(
                    R.plurals.commit_with_deletions,
                    resp.stats.deletions.toInt(),
                    resp.stats.deletions,
                ),
                style = MaterialTheme.typography.body2,
                color = commitDeletionColor,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade(
                        highlightColor = PlaceholderDefaults.fadeHighlightColor(
                            backgroundColor = commitDeletionColor
                        )
                    )
                )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
        }
    }
}

@ExperimentalCoilApi
@Preview(
    name = "ItemCommitDetailsPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun ItemCommitDetailsPreview(
    @PreviewParameter(
        provider = CommitResponseProvider::class,
        limit = 1
    )
    resp: CommitResponse
) {
    ItemCommitDetails(
        resp = resp,
        enablePlaceholder = false
    )
}

@Preview(
    name = "CommitFileItem",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CommitFileItemPreview(
    @PreviewParameter(
        provider = CommitFileProvider::class,
        limit = 1
    )
    file: CommitFile
) {
    CommitFileItem(
        file = file,
        enablePlaceholder = false
    )
}