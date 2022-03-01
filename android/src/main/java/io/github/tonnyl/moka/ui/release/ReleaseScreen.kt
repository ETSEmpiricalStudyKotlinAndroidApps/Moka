package io.github.tonnyl.moka.ui.release

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.util.ReleaseProvider
import io.tonnyl.moka.graphql.fragment.Release
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun ReleaseScreen(
    login: String,
    repoName: String,
    tagName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            ReleaseViewModel(
                extra = ReleaseViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repoName = repoName,
                    tagName = tagName
                )
            )
        }
    )

    val releaseResource by viewModel.release.observeAsState()

    val release = releaseResource?.data

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val isLoading = releaseResource?.status == Status.LOADING

        val releasePlaceholder = remember {
            ReleaseProvider().values.first()
        }

        when {
            isLoading || release != null -> {
                ReleaseScreenContent(
                    contentPaddings = contentPadding,
                    login = login,
                    repoName = repoName,
                    release = release ?: releasePlaceholder,
                    enablePlaceholder = isLoading
                )
            }
            else -> {
                EmptyScreenContent(
                    titleId = if (releaseResource?.status == Status.ERROR) {
                        R.string.common_error_requesting_data
                    } else {
                        R.string.common_no_data_found
                    },
                    action = viewModel::refresh,
                    throwable = releaseResource?.e
                )
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.release)) },
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
private fun ReleaseScreenContent(
    contentPaddings: PaddingValues,
    login: String,
    repoName: String,
    release: Release,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(paddingValues = contentPaddings)
    ) {
        RepositoryOwner(
            avatarUrl = release.repository.owner.repositoryOwner.avatarUrl,
            login = login,
            repoName = repoName,
            enablePlaceholder = enablePlaceholder
        )
        Text(
            text = release.tagName,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(horizontal = ContentPaddingLargeSize)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = ContentPaddingLargeSize,
                end = ContentPaddingLargeSize,
                top = ContentPaddingLargeSize
            )
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(
                        id = R.string.released_by_and_when,
                        release.author?.actor?.login ?: "ghost",
                        DateUtils.getRelativeTimeSpanString(
                            release.createdAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ).toString()
                    ),
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
        if (enablePlaceholder) {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            Box(
                modifier = Modifier
                    .height(height = ContentPaddingLargeSize)
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        } else {
            if (release.descriptionHTML.isNullOrEmpty()) {
                EmptyReadmeText(enablePlaceholder = enablePlaceholder)
            } else {
                var webView by remember { mutableStateOf<ThemedWebView?>(null) }
                DisposableEffect(key1 = webView, key2 = enablePlaceholder) {
                    webView?.loadReleaseData(release.descriptionHTML!!)
                    onDispose {
                        webView?.stopLoading()
                    }
                }
                AndroidView(
                    factory = { ThemedWebView(it) },
                    modifier = Modifier.padding(all = ContentPaddingMediumSize)
                ) {
                    webView = it
                }
            }
        }
        ReactionGroupComponent(
            groups = release.reactionGroups.orEmpty().map { it.reactionGroup },
            tailingReactButton = true,
            viewerCanReact = release.viewerCanReact,
            react = {},
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.padding(
                start = ContentPaddingLargeSize,
                end = ContentPaddingLargeSize,
                bottom = ContentPaddingLargeSize,
                top = if (enablePlaceholder) {
                    ContentPaddingLargeSize
                } else {
                    0.dp
                }
            )
        )
        InfoListItem(
            leadingRes = R.string.release_tag,
            trailing = release.tagName,
            enablePlaceholder = enablePlaceholder
        )
        release.tagCommit?.abbreviatedOid?.let {
            InfoListItem(
                leadingRes = R.string.commit,
                trailing = it,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.clickable(
                    enabled = !enablePlaceholder
                ) {
                    Screen.Commit.navigate(
                        navController = navController,
                        login = login,
                        repoName = repoName,
                        ref = it
                    )
                }
            )
        }
        InfoListItem(
            leadingRes = R.string.release_assets,
            trailing = release.releaseAssets.totalCount.toString(),
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(enabled = !enablePlaceholder && release.releaseAssets.totalCount > 0) {
                navController.navigate(
                    route = Screen.ReleaseAssets.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                        .replace("{${Screen.ARG_TAG_NAME}}", release.tagName)
                )
            }
        )
    }
}